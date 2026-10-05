# Medication Lot Tracking System

## Project goal
Track medication lots from receipt through dispensing, support FEFO picking, flag cold-chain excursions, create child lots through repackaging, and trace manufacturer recalls through the complete lot genealogy.

## Technology
- Java 21
- Spring Boot 3.5.5
- Spring Web / REST
- Spring Data JPA
- H2 database (file based, no MySQL setup required)
- Maven
- HTML/CSS/JavaScript frontend

## Main features
1. Receive medication lots.
2. FEFO: earliest-expiring eligible stock is selected first.
3. Cold-chain excursion: lot becomes QUARANTINED and is excluded from FEFO.
4. Repackaging: parent lot -> child lot genealogy.
5. Split operation uses the same genealogy mechanism.
6. Merge endpoint can be added through the service layer for multiple source lots.
7. Dispensing records preserve lot -> patient -> location traceability.
8. Recall impact walks the lot genealogy graph and returns affected patients, locations, lots, and action items.
9. Database indexes are included for lot number, expiry, status, parent/child genealogy and dispensing lookup.
10. Simple browser dashboard is included.

## Requirements
Install:
- JDK 21 or newer
- Maven 3.9+
- VS Code
- Extension Pack for Java (recommended)

Check:
    java -version
    mvn -version

## Run in VS Code
1. Extract the ZIP.
2. Open the extracted `Medication_Lot_Tracking_System` folder in VS Code.
3. Open Terminal -> New Terminal.
4. Run:
       mvn spring-boot:run
5. Wait for:
       Started MedicationLotTrackingApplication
6. Open:
       http://localhost:8080

H2 console:
       http://localhost:8080/h2-console
JDBC URL:
       jdbc:h2:file:./data/medtrack
User: sa
Password: leave blank

## Important demo APIs

### View lots
GET /api/lots

### FEFO
GET /api/fefo?medication=Paracetamol%20500mg&quantity=20

### Dispense
POST /api/dispense
JSON:
{
  "medication": "Paracetamol 500mg",
  "mrn": "MRN1004",
  "quantity": 10
}

### Cold-chain excursion
POST /api/lots/LOT-INS-001/cold-chain

### Repackage
POST /api/lots/repackage
JSON:
{
  "parentLot": "LOT-INS-001",
  "childLotNumber": "LOT-INS-001-CHILD-001",
  "quantity": 20
}

### Recall impact
POST /api/recall/impact?lot=LOT-PARA-001

## Demo data
The application automatically creates:
- 4 patients
- 4 medication lots
- a child lot created by repackaging
- 3 dispensing transactions

This means the dashboard has data immediately after startup.

## How the recall algorithm works
A lot genealogy is represented as a graph:
ROOT LOT -> CHILD LOT -> GRANDCHILD LOT ...

For a recall, the service starts at the recalled root lot and performs a breadth-first traversal through `lot_genealogy`. Every reachable lot is considered impacted. Dispensing records are then looked up using the impacted lot set.

This avoids recursively searching the entire application for every patient and keeps the genealogy explicit.

## TL explanation
"Medication Lot Tracking provides end-to-end traceability from receipt to patient. FEFO selects stock by earliest expiry while excluding quarantined or cold-chain excursion lots. Repackaging and splitting create explicit parent-child genealogy records, so a recall can traverse the graph and identify every downstream lot, patient and location. Indexed lot and dispensing tables keep recall and traceability queries efficient."

## Suggested future production enhancements
- PostgreSQL or SQL Server
- Redis/cache for hot recall paths
- event-driven receiving/dispensing using Kafka/RabbitMQ
- immutable audit log
- role-based access control
- barcode/GS1 scanning
- temperature sensor ingestion
- scheduled expiry alerts
- bulk recall action workflow
- database recursive CTE for very large genealogy graphs
- optimistic locking on inventory updates
- idempotency keys for dispensing/repackaging
- automated integration tests
