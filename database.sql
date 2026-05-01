

CREATE DATABASE vehicle_identification;

CREATE TABLE customer (
    customer_id SERIAL PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    address     VARCHAR(255),
    phone       VARCHAR(30),
    email       VARCHAR(120)
);

CREATE TABLE vehicle (
    vehicle_id          SERIAL PRIMARY KEY,
    registration_number VARCHAR(30) UNIQUE NOT NULL,
    make                VARCHAR(80)  NOT NULL,
    model               VARCHAR(80),
    year                INT,
    owner_id            INT REFERENCES customer(customer_id) ON DELETE SET NULL
);

CREATE TABLE servicerecord (
    service_id   SERIAL PRIMARY KEY,
    vehicle_id   INT REFERENCES vehicle(vehicle_id) ON DELETE CASCADE,
    service_date DATE,
    service_type VARCHAR(100),
    description  TEXT,
    cost         NUMERIC(12,2)
);

CREATE TABLE policereport (
    report_id    SERIAL PRIMARY KEY,
    vehicle_id   INT REFERENCES vehicle(vehicle_id) ON DELETE CASCADE,
    report_date  DATE,
    report_type  VARCHAR(60),
    description  TEXT,
    officer_name VARCHAR(120)
);

CREATE TABLE violation (
    violation_id   SERIAL PRIMARY KEY,
    vehicle_id     INT REFERENCES vehicle(vehicle_id) ON DELETE CASCADE,
    violation_date DATE,
    violation_type VARCHAR(100),
    fine_amount    NUMERIC(10,2),
    status         VARCHAR(20) DEFAULT 'Unpaid'
);

-- ── Views ─────────────────────────────────────────────────
CREATE VIEW v_vehicle_details AS
    SELECT  v.vehicle_id,
            v.registration_number,
            v.make,
            v.model,
            v.year,
            c.customer_id AS owner_id,
            c.name        AS owner_name,
            c.phone       AS owner_phone,
            c.email       AS owner_email
    FROM    vehicle v
    LEFT JOIN customer c ON v.owner_id = c.customer_id;

-- Unpaid violations summary
CREATE VIEW v_unpaid_violations AS
    SELECT  vl.violation_id,
            v.registration_number,
            vl.violation_date,
            vl.violation_type,
            vl.fine_amount,
            vl.status
    FROM    violation vl
    JOIN    vehicle v ON vl.vehicle_id = v.vehicle_id
    WHERE   vl.status = 'Unpaid';

-- Police report summary
CREATE VIEW v_police_summary AS
    SELECT  pr.report_id,
            v.registration_number,
            pr.report_date,
            pr.report_type,
            pr.officer_name,
            pr.description
    FROM    policereport pr
    JOIN    vehicle v ON pr.vehicle_id = v.vehicle_id;

-- ── Stored Procedures ─────────────────────────────────────
-- Add a vehicle with owner lookup
CREATE OR REPLACE PROCEDURE sp_add_vehicle(
    p_reg   VARCHAR,
    p_make  VARCHAR,
    p_model VARCHAR,
    p_year  INT,
    p_owner INT
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO vehicle (registration_number, make, model, year, owner_id)
    VALUES (p_reg, p_make, p_model, p_year, p_owner);
END;
$$;

-- Mark a violation as paid
CREATE OR REPLACE PROCEDURE sp_pay_violation(p_violation_id INT)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE violation SET status = 'Paid' WHERE violation_id = p_violation_id;
END;
$$;

-- ── Seed data

INSERT INTO customer (name, address, phone, email) VALUES
    ('Thato Sekhnatso',   'Sebothoane, Leribe',    '+266 5812 3456', 'sekhantso@gmail.com'),
    ('Lineo Makaja', 'America Lisemeng II, Leribe',   '+266 6234 5678', 'lineo@gmail.com'),
    ('Moroesi Khampepe',    'Ha-Morobe, Botha-Bothe', '+266 5945 6789', 'khampepe@gmail.com');

INSERT INTO vehicle (registration_number, make, model, year, owner_id) VALUES
    ('C 123 LS', 'Toyota',  'GR GT',   2019, 1),
    ('C 73 LS', 'Nissan',  'GTR R35',   2021, 2),
    ('L 789 LS', 'Hyundai', 'Velosta N',  2020, 3),
    ('M 321 LS', 'Ford',    'Focus RS',  2022, 1);

INSERT INTO servicerecord (vehicle_id, service_date, service_type, description, cost) VALUES
    (1, '2025-01-10', 'Oil Change',    'Full synthetic 5W-30',  550.00),
    (1, '2025-03-20', 'Tyre Rotation', 'All four tyres rotated', 200.00),
    (2, '2025-02-14', 'Brake Service', 'Front pads replaced',   1200.00);

INSERT INTO policereport (vehicle_id, report_date, report_type, description, officer_name) VALUES
    (3, '2025-04-01', 'Accident', 'Minor fender bender on Main Rd', 'Sgt. Molefe'),
    (4, '2025-05-15', 'Theft',    'Vehicle reported stolen',        'Cst. Letsie');

INSERT INTO violation (vehicle_id, violation_date, violation_type, fine_amount, status) VALUES
    (1, '2025-03-05', 'Speeding',       500.00, 'Unpaid'),
    (2, '2025-04-18', 'No Seatbelt',    200.00, 'Paid'),
    (3, '2025-05-22', 'Illegal Parking', 150.00, 'Unpaid');
