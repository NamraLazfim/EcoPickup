# EcoPickup – Community Waste and Recycling Manager

CSC61204 Software Construction, Group 4, Scenario 3 (SDG 11.6).

EcoPickup lets Petaling Jaya residents book recycling and bulky-waste pickups, lets collectors log completed collections, and awards residents reward points. MBPJ administrators assign pickups, set the reward rule and run recycling campaigns.

## Packages

| Package | Classes | Role |
| --- | --- | --- |
| `com.ecopickup.model` | User, Resident, Collector, PickupRequest (+ Builder), CollectionRecord, RewardAccount, Address, MaterialType, PickupStatus | Domain model (core logic) |
| `com.ecopickup.reward` | RewardStrategy, PerKilogramReward, FlatRateReward, CampaignBonusReward | Reward rules (Strategy pattern) |
| `com.ecopickup.service` | PickupService, AuthService, Account, Role, PasswordHasher, PointsEntry, MaterialStat | Business logic and sign-in |
| `com.ecopickup.api` | AddressService, NominatimAddressService, AddressLookupException, JsonFields | Address lookup through OpenStreetMap Nominatim |
| `com.ecopickup.gui` | MainFrame, PickupController, AuthController, BookingPanel, PickupListPanel, CollectorTaskPanel, AdminDashboardPanel, RewardSettingsPanel and supporting panels | Java Swing GUI (MVC view and controller) |

## Design patterns

- **Builder**: `PickupRequest.Builder` takes the required details (resident, address, material, preferred date) in its constructor, chains the optional ones (estimated weight, notes, access instructions), and checks everything in `build()`.
- **Strategy**: `PickupService` holds a `RewardStrategy`. The admin can switch between per-kilogram, flat-rate and campaign-bonus rules at runtime through `setRewardStrategy()`, without changing any other class.

## Build and run

Requires Java 17+ and Maven (or open the folder as a Maven project in IntelliJ or NetBeans).

```
mvn compile               # build
mvn checkstyle:check      # clean-code rules from config/checkstyle.xml
mvn test                  # JUnit 5 tests; JaCoCo report in target/site/jacoco/index.html
```

To start the app, run `com.ecopickup.gui.MainFrame` from your IDE, or after `mvn compile`:

```
java -cp target/classes com.ecopickup.gui.MainFrame
```

The app starts with demo data. Every demo account uses the password `password123`:

| Role | Email |
| --- | --- |
| MBPJ administrator | admin@gmail.com |
| Resident | resident@gmail.com, resident2@gmail.com |
| Collector | collector@gmail.com, collector2@gmail.com |

Address lookup needs an internet connection. If Nominatim can't be reached, the booking is saved with the address marked as unverified.

## Example

```java
PickupService pickupService = new PickupService(new PerKilogramReward());
Address address = addressService.lookup("12 Jalan SS2/24, Petaling Jaya");

PickupRequest request = new PickupRequest.Builder(resident, address, MaterialType.PLASTIC, LocalDate.now().plusDays(2))
        .estimatedWeight(12.5)
        .notes("Gate code 1234")
        .build();
pickupService.submitRequest(request);
pickupService.assignCollector(request.getId(), collector);
int points = pickupService.completePickup(request.getId(), 5.0);   // 5 kg plastic = 50 points
```

## Still to do

- Unit tests in `src/test/java` (report section 4.2): at least 25 JUnit 5 tests and 75% JaCoCo line coverage
- GitHub Actions workflow in `.github/workflows` so pull requests are checked by CI
- Executable `.jar` for submission
