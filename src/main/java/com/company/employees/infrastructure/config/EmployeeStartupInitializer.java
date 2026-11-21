package com.company.employees.infrastructure.config;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
@IfBuildProfile("prod")
public class EmployeeStartupInitializer {

    private static final Logger LOGGER = Logger.getLogger(EmployeeStartupInitializer.class);

    private final EntityManager entityManager;

    @Inject
    public EmployeeStartupInitializer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    void resetEmployees(@Observes StartupEvent event) {
        entityManager.createNativeQuery("DELETE FROM employees").executeUpdate();
        int inserted = entityManager.createNativeQuery("""
                INSERT INTO employees (
                    id, first_name, second_name, paternal_surname, maternal_surname,
                    age, sex, birth_date, position, created_at, active
                )
                VALUES
                    (1, 'Elena', 'Isabel', 'Morales', 'Rivera',
                     TIMESTAMPDIFF(YEAR, '1992-02-14', CURRENT_DATE), 'FEMALE',
                     '1992-02-14', 'Project Coordinator', CURRENT_TIMESTAMP(6), TRUE),
                    (2, 'Diego', 'Luis', 'Navarro', 'Cruz',
                     TIMESTAMPDIFF(YEAR, '1996-12-20', CURRENT_DATE), 'MALE',
                     '1996-12-20', 'Software Engineer', CURRENT_TIMESTAMP(6), TRUE),
                    (3, 'Camila', 'Sofia', 'Reyes', 'Castillo',
                     TIMESTAMPDIFF(YEAR, '1985-06-03', CURRENT_DATE), 'FEMALE',
                     '1985-06-03', 'Human Resources Specialist', CURRENT_TIMESTAMP(6), TRUE),
                    (4, 'Mateo', 'Andres', 'Fuentes', 'Rojas',
                     TIMESTAMPDIFF(YEAR, '2002-03-28', CURRENT_DATE), 'MALE',
                     '2002-03-28', 'Support Analyst', CURRENT_TIMESTAMP(6), TRUE),
                    (5, 'Valeria', 'Lucia', 'Campos', 'Herrera',
                     TIMESTAMPDIFF(YEAR, '1988-11-29', CURRENT_DATE), 'FEMALE',
                     '1988-11-29', 'Business Analyst', CURRENT_TIMESTAMP(6), TRUE)
                """).executeUpdate();
        if (inserted != 5) {
            throw new IllegalStateException("Expected to seed 5 employees, but inserted " + inserted + ".");
        }
        LOGGER.info("Reset employee data to 5 sample records.");
    }
}
