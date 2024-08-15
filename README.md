# dpd-gfarkas

A backend oldalon 3 endpointot készítettem, a CRUD ajánlás figyelembevételével:
- addCustomer - új ügyfél létrehozása - PUT method
- getCustomer/{id} - ügyfél lekérdezése - GET method
- depersonalizeCustomer - ügyfél GDPR szerinti adattörlése - PATCH method

A depersonalization részhez tartozó mapping a property file-ban definiálható. Itt megadhatjuk, hogy mely értékeket írjuk át és milyen false értékekre.

Az adatbázis felépítése flyway használatával történik, a tesztelés szintén a flyway-t használja, csak in-memory adatbázissal.

A dockerizációhoz docker-compose.yml file-t készítettem, ami a 
docker compose up
utasítással indítható.
