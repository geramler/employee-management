package com.company.employees.application.mapper;

import com.company.employees.application.dto.request.CreateEmployeeRequest;
import com.company.employees.application.dto.request.UpdateEmployeeRequest;
import com.company.employees.application.dto.response.EmployeeResponse;
import com.company.employees.domain.entity.Employee;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmployeeMapper {

    public Employee toEntity(CreateEmployeeRequest request) {
        Employee employee = new Employee();
        employee.setFirstName(request.firstName);
        employee.setSecondName(request.secondName);
        employee.setPaternalSurname(request.paternalSurname);
        employee.setMaternalSurname(request.maternalSurname);
        employee.setAge(request.age);
        employee.setSex(request.sex);
        employee.setBirthDate(request.birthDate);
        employee.setPosition(request.position);
        employee.setActive(request.active == null || request.active);
        return employee;
    }

    public void updateEntity(Employee employee, UpdateEmployeeRequest request) {
        if (request.isFirstNameSpecified() || request.firstName != null) {
            employee.setFirstName(request.firstName);
        }
        if (request.isSecondNameSpecified()) {
            employee.setSecondName(request.secondName);
        }
        if (request.isPaternalSurnameSpecified() || request.paternalSurname != null) {
            employee.setPaternalSurname(request.paternalSurname);
        }
        if (request.isMaternalSurnameSpecified() || request.maternalSurname != null) {
            employee.setMaternalSurname(request.maternalSurname);
        }
        if (request.isAgeSpecified() || request.age != null) {
            employee.setAge(request.age);
        }
        if (request.isSexSpecified() || request.sex != null) {
            employee.setSex(request.sex);
        }
        if (request.isBirthDateSpecified() || request.birthDate != null) {
            employee.setBirthDate(request.birthDate);
        }
        if (request.isPositionSpecified() || request.position != null) {
            employee.setPosition(request.position);
        }
        if (request.isActiveSpecified() || request.active != null) {
            employee.setActive(request.active == null || request.active);
        }
    }

    public EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getSecondName(),
                employee.getPaternalSurname(),
                employee.getMaternalSurname(),
                employee.getAge(),
                employee.getSex(),
                employee.getBirthDate(),
                employee.getPosition(),
                employee.getCreatedAt(),
                employee.isActive()
        );
    }
}
