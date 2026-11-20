CREATE TABLE employees (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    second_name VARCHAR(100),
    paternal_surname VARCHAR(100) NOT NULL,
    maternal_surname VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL,
    sex VARCHAR(10) NOT NULL,
    birth_date DATE NOT NULL,
    position VARCHAR(150) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT chk_employees_age CHECK (age > 0),
    CONSTRAINT chk_employees_sex CHECK (sex IN ('MALE', 'FEMALE', 'OTHER'))
);
