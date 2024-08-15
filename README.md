# dpd-gfarkas

A specifikáció alapján a backend oldalon 3 endpointra van szükség (a feladatod a CRUD ajánlás alapján készítettem):
- addCustomer - új ügyfél létrehozása - PUT method
- getCustomer/{id} - ügyfél lekérdezése - GET method
- depersonalizeCustomer - úgyfél GDPR szerinti adattörlése - PATCH method

A depersonalization részhez tartozó mapping a property file-ban definiálható. Itt megadhatjuk, hogy mely értékeket írjuk át és milyen false értékekre.

Az adatbázis felépítése flyway használatával történik, a tesztelés szintén a flyway-t használja, csak in-memory adatbázissal.
