package com.company.employees.application.service;

import com.company.employees.application.dto.request.CreateEmployeeRequest;
import com.company.employees.application.dto.request.UpdateEmployeeRequest;
import com.company.employees.application.dto.response.EmployeeResponse;
import com.company.employees.application.mapper.EmployeeMapper;
import com.company.employees.domain.entity.Employee;
import com.company.employees.domain.exception.EmployeeNotFoundException;
import com.company.employees.domain.exception.EmployeeValidationException;
import com.company.employees.infrastructure.repository.EmployeeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    @Inject
    public EmployeeService(EmployeeRepository repository, EmployeeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<EmployeeResponse> findAll() {
        return repository.listAll().stream().map(mapper::toResponse).toList();
    }

    public EmployeeResponse findById(Long id) {
        Employee employee = repository.findById(id);
        if (employee == null) {
            throw new EmployeeNotFoundException(id);
        }
        return mapper.toResponse(employee);
    }

    @Transactional
    public List<EmployeeResponse> create(List<CreateEmployeeRequest> requests) {
        List<Employee> employees = requests.stream()
                .map(mapper::toEntity)
                .toList();
        employees.forEach(this::validateEmployee);
        employees.forEach(repository::persist);
        return employees.stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest request) {
        Employee employee = repository.findById(id);
        if (employee == null) {
            throw new EmployeeNotFoundException(id);
        }
        mapper.updateEntity(employee, request);
        validateEmployee(employee);
        return mapper.toResponse(employee);
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = repository.findById(id);
        if (employee == null) {
            throw new EmployeeNotFoundException(id);
        }
        repository.delete(employee);
    }

    public List<EmployeeResponse> searchByName(String name) {
        if (name == null || name.isBlank()) {
            throw new EmployeeValidationException(
                    "Search name must not be blank.",
                    Map.of("name", "must not be blank")
            );
        }
        return repository.searchByName(name).stream().map(mapper::toResponse).toList();
    }

    private void validateEmployee(Employee employee) {
        LocalDate today = LocalDate.now();
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        if (isBlank(employee.getFirstName())) fieldErrors.put("firstName", "must not be blank");
        if (isBlank(employee.getPaternalSurname())) fieldErrors.put("paternalSurname", "must not be blank");
        if (isBlank(employee.getMaternalSurname())) fieldErrors.put("maternalSurname", "must not be blank");
        if (employee.getAge() == null || employee.getAge() <= 0) fieldErrors.put("age", "must be greater than zero");
        if (employee.getSex() == null) fieldErrors.put("sex", "must not be null");
        if (employee.getBirthDate() == null || !employee.getBirthDate().isBefore(today)) {
            fieldErrors.put("birthDate", "must be in the past");
        }
        if (isBlank(employee.getPosition())) fieldErrors.put("position", "must not be blank");

        if (employee.getAge() != null && employee.getAge() > 0 && employee.getBirthDate() != null
                && employee.getBirthDate().isBefore(today)
                && employee.getAge() != Period.between(employee.getBirthDate(), today).getYears()) {
            fieldErrors.put("age", "must match the employee's birth date");
        }
        if (!fieldErrors.isEmpty()) {
            throw new EmployeeValidationException("Employee data is invalid.", fieldErrors);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
