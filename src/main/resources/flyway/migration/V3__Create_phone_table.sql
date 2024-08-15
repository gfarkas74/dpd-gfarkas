/**
 * Author:  FarkasGábor
 * Created: Aug 13, 2024
 */

CREATE TABLE phone (
    phone_id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    country_code INTEGER NOT NULL,
    phone_number BIGINT NOT NULL,
    CONSTRAINT fk_phone_customer
        FOREIGN KEY(customer_id) 
            REFERENCES customer(customer_id));
