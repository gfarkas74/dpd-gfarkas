/**
 * Author:  FarkasGábor
 * Created: Aug 13, 2024
 */

CREATE TABLE address (
    address_id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    city VARCHAR(50) NOT NULL,
    street VARCHAR(50) NOT NULL,
    house_number VARCHAR(50) NOT NULL,
    zip INTEGER NOT NULL,
    CONSTRAINT fk_address_customer
        FOREIGN KEY(customer_id) 
            REFERENCES customer(customer_id));
