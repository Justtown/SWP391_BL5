USE argo_management_system;

DROP TABLE IF EXISTS service_orders;

CREATE TABLE service_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    contract_code VARCHAR(100) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20),
    customer_address TEXT,
    machine_id INT,
    service_description TEXT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(50) DEFAULT 'PENDING',
    total_cost DECIMAL(15, 2),
    created_by INT NOT NULL,
    approved_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_status ON service_orders(status);
CREATE INDEX idx_created_by ON service_orders(created_by);
CREATE INDEX idx_contract_code ON service_orders(contract_code);
