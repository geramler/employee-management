package com.company.employees.infrastructure.repository;

import com.company.employees.domain.entity.Employee;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class EmployeeRepository implements PanacheRepository<Employee> {

    public List<Employee> searchByName(String name) {
        String escapedName = name.toLowerCase(Locale.ROOT).trim()
                .replace("/", "//")
                .replace("%", "/%")
                .replace("_", "/_");
        String pattern = "%" + escapedName + "%";
        return list(
                "lower(firstName) like ?1 escape '/' "
                        + "or lower(secondName) like ?1 escape '/' "
                        + "or lower(paternalSurname) like ?1 escape '/' "
                        + "or lower(maternalSurname) like ?1 escape '/'",
                pattern
        );
    }
}
