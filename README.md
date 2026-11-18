# EcoPickup – Community Waste and Recycling Manager

CSC61204 Software Construction, Group 4, Scenario 3 (SDG 11.6).

Core logic for section 4.1. Matches `UML/WasteManager_ClassDiagram.puml`.

## Packages

| Package | Classes |
| --- | --- |
| `com.ecopickup.model` | User, Resident, Collector, PickupRequest (+ Builder), CollectionRecord, RewardAccount, Address, MaterialType, PickupStatus |
| `com.ecopickup.reward` | RewardStrategy, PerKilogramReward, FlatRateReward, CampaignBonusReward |


Still to add:
- `com.ecopickup.service`PickupService 
- `com.ecopickup.api`AddressService, AddressLookupException 
- `com.ecopickup.api.NominatimAddressService` (section 5.2)
- `com.ecopickup.gui`: BookingPanel, PickupListPanel, CollectorTaskPanel, PickupController (section 5.1)
- Unit tests in `src/test/java` (section 4.2)

## Build and check

Requires Java 17+ and Maven (or open the folder as a Maven project in IntelliJ or NetBeans).

```
mvn compile               # build
mvn checkstyle:check      # clean-code rules from config/checkstyle.xml
mvn test                  # JUnit 5 tests; JaCoCo report in target/site/jacoco/index.html
```

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
pickupService.completePickup(request.getId(), 5.0);   // 5 kg plastic = 50 points
```
