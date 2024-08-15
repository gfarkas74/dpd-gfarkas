/**
 * Author:  FarkasGábor
 * Created: Aug 13, 2024
 */

CREATE TABLE customer (
    customer_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    date_birth TIMESTAMP NOT NULL,
    place_birth VARCHAR(100) NOT NULL,
    name_mother VARCHAR(100) NOT NULL,
    taj INTEGER,
    tax INTEGER,
    email VARCHAR(50) NOT NULL
);
