package com.ecopickup.gui;

import com.ecopickup.service.Account;
import com.ecopickup.api.AddressLookupException;
import com.ecopickup.api.AddressService;
import com.ecopickup.service.MaterialStat;
import com.ecopickup.service.PickupService;
import com.ecopickup.service.PointsEntry;
import com.ecopickup.model.Address;
import com.ecopickup.model.CollectionRecord;
import com.ecopickup.model.Collector;
import com.ecopickup.model.MaterialType;
import com.ecopickup.model.PickupRequest;
import com.ecopickup.model.PickupStatus;
import com.ecopickup.model.Resident;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

// Controller between the views and the service layer (the C in MVC).
final class PickupController {

    private static final double MAX_ACTUAL_WEIGHT_KG = 500;

    private final AuthController auth;
    private final PickupService service;
    private final AddressService addressService;
    private final RewardSettings settings;

    PickupController(AuthController auth, PickupService service, AddressService addressService,
                            RewardSettings settings) {
        this.auth = auth;
        this.service = service;
        this.addressService = addressService;
        this.settings = settings;
        service.setRewardStrategy(settings.createStrategy());
    }

    // ---------- resident: booking ----------

    PickupRequest submitBooking(BookingForm form) {
        Input.requireText(form.address(), "Enter the pickup address.");
        // check the form first so the API is not called for a form that is wrong anyway
        build(builderFor(form, Address.unverified(form.address().trim())));
        Address address = checkAddress(form.address()).address();
        PickupRequest request = build(builderFor(form, address));
        try {
            service.submitRequest(request);
        } catch (IllegalStateException duplicate) {
            throw new IllegalArgumentException(duplicate.getMessage() + ".");
        }
        return request;
    }

    AddressCheck checkAddress(String typedAddress) {
        Input.requireText(typedAddress, "Enter the pickup address.");
        try {
            Address found = addressService.lookup(typedAddress.trim());
            return new AddressCheck(found, "Address found: " + found.getDisplayName());
        } catch (AddressLookupException failure) {
            if (failure.getReason() == AddressLookupException.Reason.NOT_FOUND) {
                throw new IllegalArgumentException(failure.getMessage());
            }
            return new AddressCheck(Address.unverified(typedAddress.trim()),
                    failure.getMessage() + " Your address will be saved as unverified.");
        }
    }

    int estimatePoints(MaterialType material, String weightText) {
        try {
            double weight = Input.positiveNumber(weightText, "weight");
            PickupRequest sample = new PickupRequest.Builder(auth.getCurrentResident(),
                    Address.unverified("estimate"), material, LocalDate.now().plusDays(1)).build();
            return service.previewPoints(new CollectionRecord(sample, null, weight, LocalDateTime.now()));
        } catch (IllegalArgumentException | IllegalStateException invalid) {
            return 0;
        }
    }

    String getHomeAddress() {
        Account account = auth.getCurrentAccount();
        return account == null ? "" : account.homeAddress();
    }

    // ---------- resident: my pickups and rewards ----------

    List<PickupRequest> getMyPickups() {
        return service.getRequestsBy(auth.getCurrentResident()).stream()
                .sorted(Comparator.comparing(PickupRequest::getPreferredDate).reversed())
                .toList();
    }

    void cancel(PickupRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Select a pickup first.");
        }
        if (!request.getStatus().canTransitionTo(PickupStatus.CANCELLED)) {
            throw new IllegalArgumentException("Only requested or assigned pickups can be cancelled.");
        }
        service.cancel(request.getId());
    }

    int countActivePickups() {
        return (int) service.getRequestsBy(auth.getCurrentResident()).stream()
                .filter(r -> r.getStatus() == PickupStatus.REQUESTED || r.getStatus() == PickupStatus.ASSIGNED)
                .count();
    }

    int getBalance() {
        Resident resident = auth.getCurrentResident();
        return resident == null ? 0 : resident.getRewardAccount().getBalance();
    }

    void redeem(String pointsText) {
        int points = Input.positiveWholeNumber(pointsText, "points");
        try {
            service.redeemPoints(auth.getCurrentResident(), points);
        } catch (IllegalStateException notEnough) {
            throw new IllegalArgumentException("You don't have enough points. Your balance is " + getBalance() + ".");
        }
    }

    List<PointsEntry> getHistory() {
        return service.getHistory(auth.getCurrentResident());
    }

    Integer getPointsEarned(PickupRequest request) {
        return service.getPointsEarned(request.getId());
    }

    Double getActualWeight(PickupRequest request) {
        CollectionRecord record = service.getRecord(request.getId());
        return record == null ? null : record.getActualWeightKg();
    }

    double getRecycledKg() {
        Resident resident = auth.getCurrentResident();
        List<PickupRequest> scope = resident == null ? service.getAllRequests() : service.getRequestsBy(resident);
        return scope.stream().map(this::getActualWeight).filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue).sum();
    }

    boolean isCampaignRunning() {
        return settings.isCampaignRunning(LocalDate.now());
    }

    // ---------- collector ----------

    List<PickupRequest> getTasks() {
        return service.getRequestsFor(auth.getCurrentCollector(), PickupStatus.ASSIGNED).stream()
                .sorted(Comparator.comparing(PickupRequest::getPreferredDate))
                .toList();
    }

    List<CollectionRecord> getCompletedTasks() {
        return service.getRequestsFor(auth.getCurrentCollector(), PickupStatus.COMPLETED).stream()
                .map(r -> service.getRecord(r.getId()))
                .sorted(Comparator.comparing(CollectionRecord::getCompletedAt).reversed())
                .toList();
    }

    double getCollectedKg() {
        return getCompletedTasks().stream().mapToDouble(CollectionRecord::getActualWeightKg).sum();
    }

    int completeTask(PickupRequest request, String weightText) {
        if (request == null) {
            throw new IllegalArgumentException("Select a task first.");
        }
        if (request.getStatus() != PickupStatus.ASSIGNED) {
            throw new IllegalArgumentException("This task is no longer open.");
        }
        double weight = Input.positiveNumber(weightText, "actual weight");
        if (weight > MAX_ACTUAL_WEIGHT_KG) {
            throw new IllegalArgumentException("The actual weight must be " + (int) MAX_ACTUAL_WEIGHT_KG
                    + " kg or less.");
        }
        return service.completePickup(request.getId(), weight);
    }

    // ---------- admin ----------

    List<PickupRequest> getRequests(PickupStatus status) {
        List<PickupRequest> found = status == null ? service.getAllRequests() : service.getRequestsWith(status);
        return found.stream().sorted(Comparator.comparing(PickupRequest::getPreferredDate)).toList();
    }

    void assign(PickupRequest request, Collector collector) {
        if (request == null) {
            throw new IllegalArgumentException("Select a request first.");
        }
        if (collector == null) {
            throw new IllegalArgumentException("Choose a collector.");
        }
        if (request.getStatus() != PickupStatus.REQUESTED) {
            throw new IllegalArgumentException("Only new requests can be assigned.");
        }
        service.assignCollector(request.getId(), collector);
    }

    int countTasks(Collector collector, PickupStatus status) {
        return service.getRequestsFor(collector, status).size();
    }

    List<MaterialStat> getMaterialStats() {
        return service.getMaterialStats();
    }

    RewardSettings getRewardSettings() {
        return settings;
    }

    void saveRewardSettings(RewardSettingsForm form) {
        int flatPoints = Input.positiveWholeNumber(form.flatPointsText(), "points per pickup");
        double multiplier = Input.positiveNumber(form.multiplierText(), "multiplier");
        if (multiplier < 1) {
            throw new IllegalArgumentException("The multiplier must be at least 1.");
        }
        LocalDate start = Input.date(form.startText(), "campaign start date");
        LocalDate end = Input.date(form.endText(), "campaign end date");
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("The campaign must end on or after its start date.");
        }
        settings.update(form.rule(), flatPoints, form.material(), multiplier, start, end);
        // Strategy pattern: swap the reward rule at runtime
        service.setRewardStrategy(settings.createStrategy());
    }

    // ---------- helpers ----------

    private PickupRequest.Builder builderFor(BookingForm form, Address address) {
        LocalDate date = Input.date(form.dateText(), "preferred date");
        PickupRequest.Builder builder = new PickupRequest.Builder(auth.getCurrentResident(), address,
                form.material(), date);
        if (!form.weightText().isEmpty()) {
            builder.estimatedWeight(Input.positiveNumber(form.weightText(), "estimated weight"));
        }
        if (!form.notes().isEmpty()) {
            builder.notes(form.notes());
        }
        if (!form.access().isEmpty()) {
            builder.accessInstructions(form.access());
        }
        return builder;
    }

    private static PickupRequest build(PickupRequest.Builder builder) {
        try {
            return builder.build();
        } catch (IllegalStateException invalid) {
            throw new IllegalArgumentException(invalid.getMessage() + ".");
        }
    }
}
