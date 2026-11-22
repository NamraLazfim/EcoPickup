package com.ecopickup.gui;

import com.ecopickup.service.AuthService;
import com.ecopickup.service.PickupService;
import com.ecopickup.service.Role;
import com.ecopickup.model.Address;
import com.ecopickup.model.Collector;
import com.ecopickup.model.MaterialType;
import com.ecopickup.model.PickupRequest;
import com.ecopickup.model.Resident;
import java.time.LocalDate;

// Demo accounts and pickups. Every demo account uses the password below.
final class DemoData {

    static final String DEMO_PASSWORD = "password123";

    private static final int TOMORROW = 1;
    private static final int IN_TWO_DAYS = 2;
    private static final int IN_THREE_DAYS = 3;
    private static final int NEXT_WEEK = 7;
    private static final String SS2_HOME = "15 Jalan SS2/24, SS2, Petaling Jaya, Selangor";

    private DemoData() {
    }

    static void load(AuthService auth, PickupService service) {
        auth.addAccount("MBPJ Admin", "admin@gmail.com", DEMO_PASSWORD, Role.ADMIN, null);
        Resident aiman = new Resident("R-101", "Aiman H.", "resident@gmail.com");
        Resident nurul = new Resident("R-102", "Nurul A.", "resident2@gmail.com");
        Collector ravi = new Collector("C-101", "Ravi K.", "collector@gmail.com", "PJU 3");
        Collector farid = new Collector("C-102", "Farid M.", "collector2@gmail.com", "SS2");
        auth.addAccount(aiman.getName(), aiman.getEmail(), DEMO_PASSWORD, Role.RESIDENT, aiman);
        auth.addAccount(nurul.getName(), nurul.getEmail(), DEMO_PASSWORD, Role.RESIDENT, nurul);
        auth.addAccount(ravi.getName(), ravi.getEmail(), DEMO_PASSWORD, Role.COLLECTOR, ravi);
        auth.addAccount(farid.getName(), farid.getEmail(), DEMO_PASSWORD, Role.COLLECTOR, farid);
        loadHistory(service, aiman, ravi);
        loadOpenRequests(service, aiman, nurul, ravi);
    }

    private static void loadHistory(PickupService service, Resident resident, Collector collector) {
        PickupRequest paper = save(service, request(resident, SS2_HOME, MaterialType.PAPER, TOMORROW, "10"));
        service.assignCollector(paper.getId(), collector);
        service.completePickup(paper.getId(), Double.parseDouble("9.2"));
        PickupRequest metal = save(service, request(resident, SS2_HOME, MaterialType.METAL, TOMORROW, "3"));
        service.assignCollector(metal.getId(), collector);
        service.completePickup(metal.getId(), Double.parseDouble("3.5"));
        PickupRequest glass = save(service, request(resident, SS2_HOME, MaterialType.GLASS, TOMORROW, ""));
        service.cancel(glass.getId());
    }

    private static void loadOpenRequests(PickupService service, Resident aiman, Resident nurul,
                                         Collector collector) {
        save(service, request(aiman, "12 Jalan SS2/24, Petaling Jaya", MaterialType.PLASTIC, IN_TWO_DAYS, "12.5"));
        PickupRequest eWaste = save(service, request(aiman, "12 Jalan SS2/24, Petaling Jaya",
                MaterialType.E_WASTE, IN_THREE_DAYS, "4"));
        service.assignCollector(eWaste.getId(), collector);
        PickupRequest metal = save(service, request(nurul, "8 Jalan PJU 3/1, Petaling Jaya",
                MaterialType.METAL, IN_TWO_DAYS, "6"));
        service.assignCollector(metal.getId(), collector);
        save(service, request(nurul, "21 Jalan SS3/9, Petaling Jaya", MaterialType.BULKY, NEXT_WEEK, ""));
        save(service, request(nurul, "30 Jalan SS4/2, Petaling Jaya", MaterialType.PAPER, IN_THREE_DAYS, "7"));
    }

    private static PickupRequest request(Resident resident, String address, MaterialType material, int daysAhead,
                                         String weight) {
        Address verified = new Address(address, Double.parseDouble("3.1186"), Double.parseDouble("101.6206"));
        PickupRequest.Builder builder = new PickupRequest.Builder(resident, verified, material,
                LocalDate.now().plusDays(daysAhead))
                .notes("Gate code 1234")
                .accessInstructions("Bags are by the back gate");
        if (!weight.isEmpty()) {
            builder.estimatedWeight(Double.parseDouble(weight));
        }
        return builder.build();
    }

    private static PickupRequest save(PickupService service, PickupRequest request) {
        service.submitRequest(request);
        return request;
    }
}
