-- Database: plateiq-db

-- DROP DATABASE IF EXISTS "plateiq-db";

CREATE DATABASE "plateiq-db"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'English_South Africa.1252'
    LC_CTYPE = 'English_South Africa.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE Users (
    user_id     SERIAL          PRIMARY KEY,
    username    VARCHAR(50)     NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    role        VARCHAR(20)     NOT NULL CHECK (role IN ('ADMIN','CUSTOMER','WORKSHOP','INSURANCE','POLICE')),
    status      VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED')),
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    last_login  TIMESTAMP,
    created_by  INT             REFERENCES Users(user_id) ON DELETE SET NULL
);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON Users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE Customer (
    customer_id     SERIAL          PRIMARY KEY,
    user_id         INT             UNIQUE REFERENCES Users(user_id) ON DELETE SET NULL,
    name            VARCHAR(100)    NOT NULL,
    address         TEXT,
    phone           VARCHAR(20),
    email           VARCHAR(100)    UNIQUE,
    id_number       VARCHAR(20)     UNIQUE,
    is_verified     BOOLEAN         DEFAULT FALSE,
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_customer_updated_at
    BEFORE UPDATE ON Customer
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();


CREATE TABLE Vehicle (
    vehicle_id          SERIAL          PRIMARY KEY,
    registration_number VARCHAR(20)     NOT NULL UNIQUE,
    engine_number       VARCHAR(50)     UNIQUE,
    chassis_number      VARCHAR(50)     UNIQUE,
    make                VARCHAR(50)     NOT NULL,
    model               VARCHAR(50)     NOT NULL,
    year                INT             NOT NULL CHECK (year BETWEEN 1900 AND EXTRACT(YEAR FROM CURRENT_DATE) + 5),
    color               VARCHAR(30),
    fuel_type           VARCHAR(20)     CHECK (fuel_type IN ('PETROL','DIESEL','ELECTRIC','HYBRID','LPG')),
    transmission        VARCHAR(20)     CHECK (transmission IN ('MANUAL','AUTOMATIC','CVT','DSG')),
    mileage             INT             DEFAULT 0 CHECK (mileage >= 0),
    status              VARCHAR(20)     DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','SOLD','SCRAPPED','STOLEN')),
    owner_id            INT             REFERENCES Customer(customer_id) ON DELETE SET NULL,
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_vehicle_updated_at
    BEFORE UPDATE ON Vehicle
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE ServiceRecord (
    service_id      SERIAL          PRIMARY KEY,
    vehicle_id      INT             NOT NULL REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    service_date    DATE            NOT NULL DEFAULT CURRENT_DATE,
    service_type    VARCHAR(100)    NOT NULL,
    description     TEXT,
    cost            DECIMAL(10,2)   NOT NULL DEFAULT 0.00 CHECK (cost >= 0),
    odometer_reading INT            CHECK (odometer_reading >= 0),
    mechanic_name   VARCHAR(100),
    next_service_due DATE,
    invoice_number  VARCHAR(50)     UNIQUE,
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Users (
    user_id     SERIAL          PRIMARY KEY,
    username    VARCHAR(50)     NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    role        VARCHAR(20)     NOT NULL CHECK (role IN ('ADMIN','CUSTOMER','WORKSHOP','INSURANCE','POLICE')),
    status      VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED')),
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    last_login  TIMESTAMP,
    created_by  INT             REFERENCES Users(user_id) ON DELETE SET NULL
);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON Users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE Customer (
    customer_id     SERIAL          PRIMARY KEY,
    user_id         INT             UNIQUE REFERENCES Users(user_id) ON DELETE SET NULL,
    name            VARCHAR(100)    NOT NULL,
    address         TEXT,
    phone           VARCHAR(20),
    email           VARCHAR(100)    UNIQUE,
    id_number       VARCHAR(20)     UNIQUE,  
    is_verified     BOOLEAN         DEFAULT FALSE,
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_customer_updated_at
    BEFORE UPDATE ON Customer
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE Vehicle (
    vehicle_id          SERIAL          PRIMARY KEY,
    registration_number VARCHAR(20)     NOT NULL UNIQUE,
    engine_number       VARCHAR(50)     UNIQUE,
    chassis_number      VARCHAR(50)     UNIQUE,
    make                VARCHAR(50)     NOT NULL,
    model               VARCHAR(50)     NOT NULL,
    year                INT             NOT NULL CHECK (year BETWEEN 1900 AND EXTRACT(YEAR FROM CURRENT_DATE) + 5),
    color               VARCHAR(30),
    fuel_type           VARCHAR(20)     CHECK (fuel_type IN ('PETROL','DIESEL','ELECTRIC','HYBRID','LPG')),
    transmission        VARCHAR(20)     CHECK (transmission IN ('MANUAL','AUTOMATIC','CVT','DSG')),
    mileage             INT             DEFAULT 0 CHECK (mileage >= 0),
    status              VARCHAR(20)     DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','SOLD','SCRAPPED','STOLEN')),
    owner_id            INT             REFERENCES Customer(customer_id) ON DELETE SET NULL,
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_vehicle_updated_at
    BEFORE UPDATE ON Vehicle
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE ServiceRecord (
    service_id      SERIAL          PRIMARY KEY,
    vehicle_id      INT             NOT NULL REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    service_date    DATE            NOT NULL DEFAULT CURRENT_DATE,
    service_type    VARCHAR(100)    NOT NULL,
    description     TEXT,
    cost            DECIMAL(10,2)   NOT NULL DEFAULT 0.00 CHECK (cost >= 0),
    odometer_reading INT            CHECK (odometer_reading >= 0),
    mechanic_name   VARCHAR(100),
    next_service_due DATE,
    invoice_number  VARCHAR(50)     UNIQUE,
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE CustomerQuery (
    query_id        SERIAL      PRIMARY KEY,
    customer_id     INT         NOT NULL REFERENCES Customer(customer_id) ON DELETE CASCADE,
    vehicle_id      INT         NOT NULL REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    query_date      DATE        NOT NULL DEFAULT CURRENT_DATE,
    query_text      TEXT        NOT NULL,
    response_text   TEXT,
    responded_by    INT         REFERENCES Users(user_id) ON DELETE SET NULL,
    responded_at    TIMESTAMP,
    status          VARCHAR(20) DEFAULT 'OPEN' CHECK (status IN ('OPEN','IN_PROGRESS','RESOLVED','CLOSED')),
    priority        VARCHAR(10) DEFAULT 'MEDIUM' CHECK (priority IN ('LOW','MEDIUM','HIGH','URGENT'))
);

CREATE TABLE InsurancePolicy (
    policy_id           SERIAL          PRIMARY KEY,
    vehicle_id          INT             NOT NULL REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    insurance_company   VARCHAR(100)    NOT NULL,
    policy_number       VARCHAR(50)     NOT NULL UNIQUE,
    start_date          DATE            NOT NULL,
    end_date            DATE            NOT NULL,
    premium_amount      DECIMAL(10,2)   CHECK (premium_amount >= 0),
    coverage_details    TEXT,
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    CHECK (end_date > start_date)
);

CREATE OR REPLACE VIEW active_insurance_policies AS
SELECT 
    *,
    (end_date >= CURRENT_DATE) AS is_active
FROM InsurancePolicy;

CREATE TABLE Claim (
    claim_id        SERIAL          PRIMARY KEY,
    policy_id       INT             NOT NULL REFERENCES InsurancePolicy(policy_id) ON DELETE CASCADE,
    claim_date      DATE            NOT NULL DEFAULT CURRENT_DATE,
    claim_amount    DECIMAL(10,2)   NOT NULL CHECK (claim_amount >= 0),
    approved_amount DECIMAL(10,2)   CHECK (approved_amount >= 0),
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING','APPROVED','REJECTED','PAID')),
    description     TEXT,
    resolved_date   DATE,
    adjusted_by     INT             REFERENCES Users(user_id) ON DELETE SET NULL
);

CREATE TABLE PoliceReport (
    report_id       SERIAL          PRIMARY KEY,
    vehicle_id      INT             NOT NULL REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    report_date     DATE            NOT NULL DEFAULT CURRENT_DATE,
    report_type     VARCHAR(50)     NOT NULL CHECK (report_type IN ('THEFT','ACCIDENT','INSPECTION','OTHER')),
    description     TEXT,
    officer_name    VARCHAR(100)    NOT NULL,
    case_number     VARCHAR(50)     UNIQUE,
    station_name    VARCHAR(100),
    is_closed       BOOLEAN         DEFAULT FALSE
);

CREATE TABLE Violation (
    violation_id    SERIAL          PRIMARY KEY,
    vehicle_id      INT             NOT NULL REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    violation_date  DATE            NOT NULL,
    violation_type  VARCHAR(100)    NOT NULL,
    fine_amount     DECIMAL(10,2)   NOT NULL DEFAULT 0.00 CHECK (fine_amount >= 0),
    status          VARCHAR(20)     NOT NULL DEFAULT 'UNPAID' CHECK (status IN ('PAID','UNPAID','DISPUTED','WAIVED')),
    paid_date       DATE,
    officer_name    VARCHAR(100),
    location        VARCHAR(200)
);


CREATE INDEX idx_users_role_status ON Users(role, status);
CREATE INDEX idx_users_username ON Users(username);

CREATE INDEX idx_customer_email ON Customer(email);
CREATE INDEX idx_customer_phone ON Customer(phone);
CREATE INDEX idx_customer_name ON Customer(name);

CREATE INDEX idx_vehicle_registration ON Vehicle(registration_number);
CREATE INDEX idx_vehicle_owner ON Vehicle(owner_id);
CREATE INDEX idx_vehicle_make_model ON Vehicle(make, model);
CREATE INDEX idx_vehicle_status ON Vehicle(status);

CREATE INDEX idx_servicerecord_vehicle ON ServiceRecord(vehicle_id);
CREATE INDEX idx_servicerecord_date ON ServiceRecord(service_date);
CREATE INDEX idx_servicerecord_next_due ON ServiceRecord(next_service_due);

CREATE INDEX idx_customerquery_customer ON CustomerQuery(customer_id);
CREATE INDEX idx_customerquery_vehicle ON CustomerQuery(vehicle_id);
CREATE INDEX idx_customerquery_status ON CustomerQuery(status);

CREATE INDEX idx_insurancepolicy_vehicle ON InsurancePolicy(vehicle_id);
CREATE INDEX idx_insurancepolicy_dates ON InsurancePolicy(start_date, end_date);
CREATE INDEX idx_insurancepolicy_company ON InsurancePolicy(insurance_company);

CREATE INDEX idx_claim_policy ON Claim(policy_id);
CREATE INDEX idx_claim_status ON Claim(status);
CREATE INDEX idx_claim_date ON Claim(claim_date);

CREATE INDEX idx_policereport_vehicle ON PoliceReport(vehicle_id);
CREATE INDEX idx_policereport_date ON PoliceReport(report_date);
CREATE INDEX idx_policereport_type ON PoliceReport(report_type);

CREATE INDEX idx_violation_vehicle ON Violation(vehicle_id);
CREATE INDEX idx_violation_status ON Violation(status);
CREATE INDEX idx_violation_date ON Violation(violation_date);


CREATE OR REPLACE VIEW vehicle_full_details AS
SELECT
    v.vehicle_id,
    v.registration_number,
    v.engine_number,
    v.chassis_number,
    v.make,
    v.model,
    v.year,
    v.color,
    v.fuel_type,
    v.transmission,
    v.mileage,
    v.status AS vehicle_status,
    c.customer_id,
    c.name          AS owner_name,
    c.phone         AS owner_phone,
    c.email         AS owner_email,
    c.address       AS owner_address,
    ip.policy_id,
    ip.insurance_company,
    ip.policy_number,
    ip.start_date   AS policy_start,
    ip.end_date     AS policy_end,
    CASE
        WHEN ip.end_date >= CURRENT_DATE THEN 'ACTIVE'
        WHEN ip.end_date IS NULL         THEN 'NO POLICY'
        ELSE                                  'EXPIRED'
    END             AS insurance_status
FROM Vehicle v
LEFT JOIN Customer c        ON v.owner_id   = c.customer_id
LEFT JOIN InsurancePolicy ip ON v.vehicle_id = ip.vehicle_id;

CREATE OR REPLACE VIEW unpaid_violations AS
SELECT
    vl.violation_id,
    vl.violation_date,
    vl.violation_type,
    vl.fine_amount,
    vl.status,
    vl.location,
    v.registration_number,
    v.make,
    v.model,
    c.name  AS owner_name,
    c.phone AS owner_phone,
    c.email AS owner_email
FROM Violation vl
JOIN Vehicle  v ON vl.vehicle_id = v.vehicle_id
LEFT JOIN Customer c ON v.owner_id = c.customer_id
WHERE vl.status IN ('UNPAID', 'DISPUTED')
ORDER BY vl.violation_date DESC;

CREATE OR REPLACE VIEW vehicle_service_summary AS
SELECT
    v.vehicle_id,
    v.registration_number,
    v.make,
    v.model,
    v.mileage AS current_mileage,
    COUNT(sr.service_id)        AS total_services,
    MAX(sr.service_date)        AS last_service_date,
    MIN(sr.next_service_due)    AS next_service_due,
    SUM(sr.cost)                AS total_service_cost,
    AVG(sr.cost)                AS avg_service_cost
FROM Vehicle v
LEFT JOIN ServiceRecord sr ON v.vehicle_id = sr.vehicle_id
GROUP BY v.vehicle_id, v.registration_number, v.make, v.model, v.mileage;

CREATE OR REPLACE VIEW expiring_policies AS
SELECT
    ip.policy_id,
    ip.policy_number,
    ip.insurance_company,
    ip.end_date,
    (ip.end_date - CURRENT_DATE) AS days_remaining,
    ip.premium_amount,
    v.registration_number,
    v.make,
    v.model,
    c.name  AS owner_name,
    c.phone AS owner_phone,
    c.email AS owner_email
FROM InsurancePolicy ip
JOIN Vehicle  v ON ip.vehicle_id = v.vehicle_id
LEFT JOIN Customer c ON v.owner_id  = c.customer_id
WHERE ip.end_date BETWEEN CURRENT_DATE AND (CURRENT_DATE + INTERVAL '60 days')
  AND ip.end_date >= CURRENT_DATE
ORDER BY ip.end_date ASC;

CREATE OR REPLACE VIEW customer_query_summary AS
SELECT
    c.customer_id,
    c.name,
    COUNT(q.query_id) AS total_queries,
    COUNT(CASE WHEN q.status = 'OPEN' THEN 1 END) AS open_queries,
    COUNT(CASE WHEN q.status = 'RESOLVED' THEN 1 END) AS resolved_queries,
    MAX(q.query_date) AS last_query_date
FROM Customer c
LEFT JOIN CustomerQuery q ON c.customer_id = q.customer_id
GROUP BY c.customer_id, c.name
ORDER BY total_queries DESC;

CREATE OR REPLACE VIEW vehicle_violation_history AS
SELECT
    v.vehicle_id,
    v.registration_number,
    v.make,
    v.model,
    COUNT(vl.violation_id) AS total_violations,
    SUM(CASE WHEN vl.status = 'PAID' THEN 1 ELSE 0 END) AS paid_violations,
    SUM(CASE WHEN vl.status = 'UNPAID' THEN 1 ELSE 0 END) AS unpaid_violations,
    SUM(CASE WHEN vl.status = 'DISPUTED' THEN 1 ELSE 0 END) AS disputed_violations,
    SUM(vl.fine_amount) AS total_fines,
    SUM(CASE WHEN vl.status = 'PAID' THEN vl.fine_amount ELSE 0 END) AS paid_fines,
    SUM(CASE WHEN vl.status = 'UNPAID' THEN vl.fine_amount ELSE 0 END) AS unpaid_fines
FROM Vehicle v
LEFT JOIN Violation vl ON v.vehicle_id = vl.vehicle_id
GROUP BY v.vehicle_id, v.registration_number, v.make, v.model;


CREATE OR REPLACE PROCEDURE register_vehicle(
    p_registration_number   VARCHAR(20),
    p_make                  VARCHAR(50),
    p_model                 VARCHAR(50),
    p_year                  INT,
    p_color                 VARCHAR(30),
    p_owner_id              INT,
    p_fuel_type             VARCHAR(20) DEFAULT NULL,
    p_transmission          VARCHAR(20) DEFAULT NULL,
    p_mileage               INT DEFAULT 0
)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_year < 1900 OR p_year > EXTRACT(YEAR FROM CURRENT_DATE) + 1 THEN
        RAISE EXCEPTION 'Invalid year. Must be between 1900 and %', EXTRACT(YEAR FROM CURRENT_DATE) + 1;
    END IF;
    
    IF EXISTS (SELECT 1 FROM Vehicle WHERE registration_number = p_registration_number) THEN
        RAISE EXCEPTION 'Vehicle with registration % already exists.', p_registration_number;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM Customer WHERE customer_id = p_owner_id) THEN
        RAISE EXCEPTION 'Customer with ID % does not exist.', p_owner_id;
    END IF;

    INSERT INTO Vehicle (registration_number, make, model, year, color, owner_id, fuel_type, transmission, mileage)
    VALUES (p_registration_number, p_make, p_model, p_year, p_color, p_owner_id, p_fuel_type, p_transmission, p_mileage);

    RAISE NOTICE 'Vehicle % registered successfully.', p_registration_number;
END;
$$;

CREATE OR REPLACE PROCEDURE add_service_record(
    p_vehicle_id        INT,
    p_service_date      DATE,
    p_service_type      VARCHAR(100),
    p_description       TEXT,
    p_cost              DECIMAL(10,2),
    p_odometer_reading  INT DEFAULT NULL,
    p_mechanic_name     VARCHAR(100) DEFAULT NULL,
    p_next_service_due  DATE DEFAULT NULL,
    p_invoice_number    VARCHAR(50) DEFAULT NULL
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Vehicle WHERE vehicle_id = p_vehicle_id) THEN
        RAISE EXCEPTION 'Vehicle with ID % does not exist.', p_vehicle_id;
    END IF;
    
    IF p_cost < 0 THEN
        RAISE EXCEPTION 'Service cost cannot be negative.';
    END IF;
    
    IF p_odometer_reading IS NOT NULL THEN
        UPDATE Vehicle SET mileage = p_odometer_reading WHERE vehicle_id = p_vehicle_id;
    END IF;

    INSERT INTO ServiceRecord (vehicle_id, service_date, service_type, description, cost, 
                               odometer_reading, mechanic_name, next_service_due, invoice_number)
    VALUES (p_vehicle_id, p_service_date, p_service_type, p_description, p_cost, 
            p_odometer_reading, p_mechanic_name, p_next_service_due, p_invoice_number);

    RAISE NOTICE 'Service record added for vehicle ID %', p_vehicle_id;
END;
$$;

CREATE OR REPLACE PROCEDURE add_police_report(
    p_vehicle_id    INT,
    p_report_date   DATE,
    p_report_type   VARCHAR(50),
    p_description   TEXT,
    p_officer_name  VARCHAR(100),
    p_case_number   VARCHAR(50) DEFAULT NULL,
    p_station_name  VARCHAR(100) DEFAULT NULL
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Vehicle WHERE vehicle_id = p_vehicle_id) THEN
        RAISE EXCEPTION 'Vehicle with ID % does not exist.', p_vehicle_id;
    END IF;
    
    IF p_report_type = 'THEFT' AND p_case_number IS NOT NULL THEN
        UPDATE Vehicle SET status = 'STOLEN' WHERE vehicle_id = p_vehicle_id;
        RAISE NOTICE 'Vehicle ID % marked as STOLEN.', p_vehicle_id;
    END IF;

    INSERT INTO PoliceReport (vehicle_id, report_date, report_type, description, officer_name, case_number, station_name)
    VALUES (p_vehicle_id, p_report_date, p_report_type, p_description, p_officer_name, p_case_number, p_station_name);

    RAISE NOTICE 'Police report added for vehicle ID %.', p_vehicle_id;
END;
$$;

CREATE OR REPLACE PROCEDURE process_claim(
    p_policy_id     INT,
    p_claim_date    DATE,
    p_claim_amount  DECIMAL(10,2),
    p_description   TEXT DEFAULT NULL,
    p_status        VARCHAR(20) DEFAULT 'PENDING'
)
LANGUAGE plpgsql AS $$
DECLARE
    v_end_date DATE;
BEGIN
    SELECT end_date INTO v_end_date FROM InsurancePolicy WHERE policy_id = p_policy_id;
    
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Policy with ID % does not exist.', p_policy_id;
    END IF;
    
    IF v_end_date < CURRENT_DATE THEN
        RAISE EXCEPTION 'Cannot process claim on expired policy. Policy ended on %.', v_end_date;
    END IF;
    
    IF p_claim_amount < 0 THEN
        RAISE EXCEPTION 'Claim amount cannot be negative.';
    END IF;

    INSERT INTO Claim (policy_id, claim_date, claim_amount, description, status)
    VALUES (p_policy_id, p_claim_date, p_claim_amount, p_description, p_status);

    RAISE NOTICE 'Claim submitted for policy ID %', p_policy_id;
END;
$$;

CREATE OR REPLACE PROCEDURE pay_violation(
    p_violation_id INT,
    p_paid_date DATE DEFAULT CURRENT_DATE
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Violation WHERE violation_id = p_violation_id) THEN
        RAISE EXCEPTION 'Violation with ID % does not exist.', p_violation_id;
    END IF;
    
    IF (SELECT status FROM Violation WHERE violation_id = p_violation_id) = 'PAID' THEN
        RAISE NOTICE 'Violation % is already PAID.', p_violation_id;
        RETURN;
    END IF;

    UPDATE Violation 
    SET status = 'PAID', 
        paid_date = p_paid_date
    WHERE violation_id = p_violation_id;
    
    RAISE NOTICE 'Violation % marked as PAID.', p_violation_id;
END;
$$;

CREATE OR REPLACE PROCEDURE transfer_ownership(
    p_vehicle_id        INT,
    p_new_owner_id      INT,
    p_transfer_date     DATE DEFAULT CURRENT_DATE
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Vehicle WHERE vehicle_id = p_vehicle_id) THEN
        RAISE EXCEPTION 'Vehicle with ID % does not exist.', p_vehicle_id;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM Customer WHERE customer_id = p_new_owner_id) THEN
        RAISE EXCEPTION 'Customer with ID % does not exist.', p_new_owner_id;
    END IF;
    
    UPDATE Vehicle 
    SET owner_id = p_new_owner_id,
        updated_at = p_transfer_date
    WHERE vehicle_id = p_vehicle_id;
    
    RAISE NOTICE 'Vehicle ID % transferred to customer ID %', p_vehicle_id, p_new_owner_id;
END;
$$;

CREATE OR REPLACE PROCEDURE add_violation(
    p_vehicle_id        INT,
    p_violation_date    DATE,
    p_violation_type    VARCHAR(100),
    p_fine_amount       DECIMAL(10,2),
    p_officer_name      VARCHAR(100) DEFAULT NULL,
    p_location          VARCHAR(200) DEFAULT NULL
)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Vehicle WHERE vehicle_id = p_vehicle_id) THEN
        RAISE EXCEPTION 'Vehicle with ID % does not exist.', p_vehicle_id;
    END IF;
    
    IF p_fine_amount < 0 THEN
        RAISE EXCEPTION 'Fine amount cannot be negative.';
    END IF;

    INSERT INTO Violation (vehicle_id, violation_date, violation_type, fine_amount, officer_name, location)
    VALUES (p_vehicle_id, p_violation_date, p_violation_type, p_fine_amount, p_officer_name, p_location);
    
    RAISE NOTICE 'Violation added for vehicle ID %', p_vehicle_id;
END;
$$;


CREATE TABLE IF NOT EXISTS AuditLog (
    log_id          SERIAL PRIMARY KEY,
    table_name      VARCHAR(50),
    record_id       INT,
    action          VARCHAR(10),
    old_data        JSONB,
    new_data        JSONB,
    changed_by      INT REFERENCES Users(user_id),
    changed_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION log_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        INSERT INTO AuditLog (table_name, record_id, action, old_data, changed_by)
        VALUES (TG_TABLE_NAME, OLD.vehicle_id, 'DELETE', row_to_json(OLD), current_user_id());
        RETURN OLD;
    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO AuditLog (table_name, record_id, action, old_data, new_data, changed_by)
        VALUES (TG_TABLE_NAME, NEW.vehicle_id, 'UPDATE', row_to_json(OLD), row_to_json(NEW), current_user_id());
        RETURN NEW;
    ELSIF (TG_OP = 'INSERT') THEN
        INSERT INTO AuditLog (table_name, record_id, action, new_data, changed_by)
        VALUES (TG_TABLE_NAME, NEW.vehicle_id, 'INSERT', row_to_json(NEW), current_user_id());
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;



INSERT INTO Users (username, password, role, status) VALUES
('admin.mokoena',    'Admin@123',     'ADMIN',     'ACTIVE'),
('admin.ntoi',       'Admin@456',     'ADMIN',     'ACTIVE'),
('admin.lefatle',    'Admin@123',     'ADMIN',     'ACTIVE'),
('admin.roto',       'Admin@456',     'ADMIN',     'ACTIVE'),
('thabo.letsie',     'User@1234',     'CUSTOMER',  'ACTIVE'),
('palesa.mosotho',   'User@1234',     'CUSTOMER',  'ACTIVE'),
('rethabile.khali',  'User@1234',     'CUSTOMER',  'ACTIVE'),
('lineo.mafoso',     'User@1234',     'CUSTOMER',  'ACTIVE'),
('mpho.sekoati',     'User@1234',     'CUSTOMER',  'ACTIVE'),
('teboho.ramoeli',   'User@1234',     'CUSTOMER',  'ACTIVE'),
('mamello.nkosi',    'User@1234',     'CUSTOMER',  'ACTIVE'),
('lerato.tau',       'User@1234',     'CUSTOMER',  'ACTIVE'),
('workshop.maseru',  'Work@1234',     'WORKSHOP',  'ACTIVE'),
('workshop.leribe',  'Work@1234',     'WORKSHOP',  'ACTIVE'),
('ins.lesotho',      'Ins@1234',      'INSURANCE', 'ACTIVE'),
('ins.continental',  'Ins@1234',      'INSURANCE', 'ACTIVE'),
('sgt.mohlomi',      'Pol@1234',      'POLICE',    'ACTIVE'),
('const.tlali',      'Pol@1234',      'POLICE',    'ACTIVE'),
('const.molefi',     'Pol@1234',      'POLICE',    'ACTIVE'),
('inactive.user',    'User@1234',     'CUSTOMER',  'INACTIVE');

UPDATE Users SET created_by = 1 WHERE user_id IN (1,2);
UPDATE Users SET created_by = 1 WHERE user_id > 2;


INSERT INTO Customer (name, address, phone, email, id_number, is_verified) VALUES
('Thabo Letsie',        'Ha Abia, Maseru 100',              '+26657812345',  'thabo.letsie@gmail.com',     'LS123456789', true),
('Palesa Mosotho',      'Teyateyaneng, Berea 200',          '+26658923456',  'palesa.m@yahoo.com',         'LS234567890', true),
('Rethabile Khali',     'Leribe Town, Leribe 300',          '+26659034567',  'rethabile.k@outlook.com',    'LS345678901', true),
('Lineo Mafoso',        'Mohales Hoek, MH 400',             '+26657145678',  'lineo.mafoso@gmail.com',     'LS456789012', false),
('Mpho Sekoati',        'Qachas Nek, QN 500',               '+26658256789',  'mpho.sekoati@gmail.com',     'LS567890123', true),
('Teboho Ramoeli',      'Butha-Buthe Town, BB 600',         '+26659367890',  'teboho.r@gmail.com',         'LS678901234', true),
('Mamello Nkosi',       'Mafeteng Central, Mafeteng 700',   '+26657478901',  'mamello.nkosi@yahoo.com',    'LS789012345', false),
('Lerato Tau',          'Ha Thetsane, Maseru 105',          '+26658589012',  'lerato.tau@gmail.com',       'LS890123456', true),
('Nthabiseng Peete',    'Maputsoe, Leribe 310',             '+26659690123',  'nthabiseng.p@gmail.com',     'LS901234567', true),
('Lebohang Mofolo',     'Roma Campus Area, Maseru 180',     '+26657701234',  'lebohang.mofolo@lu.ac.ls',   'LS012345678', true);

UPDATE Customer SET user_id = 3 WHERE customer_id = 1;
UPDATE Customer SET user_id = 4 WHERE customer_id = 2;
UPDATE Customer SET user_id = 5 WHERE customer_id = 3;
UPDATE Customer SET user_id = 6 WHERE customer_id = 4;
UPDATE Customer SET user_id = 7 WHERE customer_id = 5;
UPDATE Customer SET user_id = 8 WHERE customer_id = 6;
UPDATE Customer SET user_id = 9 WHERE customer_id = 7;
UPDATE Customer SET user_id = 10 WHERE customer_id = 8;
UPDATE Customer SET user_id = NULL WHERE customer_id IN (9,10);  


INSERT INTO Vehicle (registration_number, make, model, year, color, owner_id, fuel_type, transmission, mileage, status) VALUES
('LS-1042-AA',  'Toyota',       'Corolla',          2018,  'Silver',      1, 'PETROL', 'MANUAL',    145000, 'ACTIVE'),
('LS-2387-BB',  'Toyota',       'Hilux',            2020,  'White',       2, 'DIESEL', 'MANUAL',    89000,  'ACTIVE'),
('LS-3901-CC',  'Volkswagen',   'Polo Vivo',        2019,  'Black',       3, 'PETROL', 'MANUAL',    112000, 'ACTIVE'),
('LS-4455-DD',  'Ford',         'Ranger',           2021,  'Blue',        4, 'DIESEL', 'AUTOMATIC', 67000,  'ACTIVE'),
('LS-5512-EE',  'Toyota',       'Land Cruiser',     2017,  'Grey',        5, 'DIESEL', 'MANUAL',    198000, 'ACTIVE'),
('LS-6623-FF',  'Hyundai',      'Tucson',           2022,  'White',       6, 'PETROL', 'AUTOMATIC', 34000,  'ACTIVE'),
('LS-7734-GG',  'Nissan',       'NP200',            2016,  'Red',         7, 'PETROL', 'MANUAL',    156000, 'ACTIVE'),
('LS-8845-HH',  'Mazda',        'CX-5',             2023,  'Pearl White', 8, 'PETROL', 'AUTOMATIC', 12000,  'ACTIVE'),
('LS-9956-II',  'Isuzu',        'D-Max',            2019,  'Orange',      9, 'DIESEL', 'MANUAL',    123000, 'ACTIVE'),
('LS-0167-JJ',  'BMW',          '3 Series',         2020,  'Black',       10, 'PETROL', 'AUTOMATIC', 78000, 'ACTIVE'),
('LS-1278-KK',  'Suzuki',       'Swift',            2021,  'Blue',        1, 'PETROL', 'MANUAL',    45000,  'ACTIVE'),
('LS-2389-LL',  'Mitsubishi',   'Pajero',           2015,  'Silver',      2, 'DIESEL', 'AUTOMATIC', 234000, 'ACTIVE'),
('LS-3490-MM',  'Toyota',       'Fortuner',         2022,  'Dark Grey',   3, 'DIESEL', 'AUTOMATIC', 56000,  'ACTIVE'),
('LS-4501-NN',  'Volkswagen',   'Amarok',           2018,  'White',       4, 'DIESEL', 'AUTOMATIC', 145000, 'ACTIVE'),
('LS-5612-OO',  'Kia',          'Sportage',         2023,  'Red',         5, 'PETROL', 'AUTOMATIC', 8900,   'ACTIVE');
