package com.company.employees.application.service;

import com.company.employees.application.dto.request.CreateEmployeeRequest;
import com.company.employees.application.dto.request.UpdateEmployeeRequest;
import com.company.employees.application.dto.response.EmployeeResponse;
import com.company.employees.application.mapper.EmployeeMapper;
import com.company.employees.domain.entity.Employee;
import com.company.employees.domain.enums.Sex;
import com.company.employees.domain.exception.EmployeeNotFoundException;
import com.company.employees.domain.exception.EmployeeValidationException;
import com.company.employees.infrastructure.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceMockitoTest {

    @Mock
    EmployeeRepository repository;

    @Mock
    EmployeeMapper mapper;

    @InjectMocks
    EmployeeService service;

    @Test
    void createsAndPersistsMappedEmployee() {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        Employee employee = validEmployee();
        EmployeeResponse response = new EmployeeResponse(
                1L,
                employee.getFirstName(),
                employee.getSecondName(),
                employee.getPaternalSurname(),
                employee.getMaternalSurname(),
                employee.getAge(),
                employee.getSex(),
                employee.getBirthDate(),
                employee.getPosition(),
                Instant.now(),
                employee.isActive()
        );
        when(mapper.toEntity(request)).thenReturn(employee);
        when(mapper.toResponse(employee)).thenReturn(response);

        List<EmployeeResponse> result = service.create(List.of(request));

        assertEquals(List.of(response), result);
        verify(repository).persist(employee);
    }

    @Test
    void validatesEntireBatchBeforePersistingAnyEmployee() {
        CreateEmployeeRequest validRequest = new CreateEmployeeRequest();
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest();
        Employee validEmployee = validEmployee();
        Employee invalidEmployee = validEmployee();
        invalidEmployee.setAge(29);
        when(mapper.toEntity(validRequest)).thenReturn(validEmployee);
        when(mapper.toEntity(invalidRequest)).thenReturn(invalidEmployee);

        assertThrows(
                EmployeeValidationException.class,
                () -> service.create(List.of(validRequest, invalidRequest))
        );

        verify(repository, never()).persist(any(Employee.class));
    }

    @Test
    void returnsExistingEmployeeAndRejectsMissingEmployee() {
        Employee employee = validEmployee();
        employee.setId(12L);
        EmployeeResponse response = responseFor(employee);
        when(repository.findById(12L)).thenReturn(employee);
        when(repository.findById(404L)).thenReturn(null);
        when(mapper.toResponse(employee)).thenReturn(response);

        assertEquals(response, service.findById(12L));
        assertThrows(EmployeeNotFoundException.class, () -> service.findById(404L));
    }

    @Test
    void updatesAndDeletesExistingEmployee() {
        Employee employee = validEmployee();
        employee.setId(12L);
        UpdateEmployeeRequest update = new UpdateEmployeeRequest();
        update.position = "Lead Developer";
        EmployeeResponse response = responseFor(employee);
        when(repository.findById(12L)).thenReturn(employee);
        when(mapper.toResponse(employee)).thenReturn(response);

        assertEquals(response, service.update(12L, update));
        verify(mapper).updateEntity(employee, update);
        service.delete(12L);
        verify(repository).delete(employee);
    }

    @Test
    void rejectsMissingEmployeeForUpdateAndDelete() {
        UpdateEmployeeRequest update = new UpdateEmployeeRequest();
        when(repository.findById(404L)).thenReturn(null);

        assertThrows(EmployeeNotFoundException.class, () -> service.update(404L, update));
        assertThrows(EmployeeNotFoundException.class, () -> service.delete(404L));
        verify(repository, never()).delete(any(Employee.class));
    }

    @Test
    void searchesWithCaseInsensitivePartialName() {
        Employee employee = validEmployee();
        employee.setPaternalSurname("Garcia");
        EmployeeResponse response = responseFor(employee);
        when(repository.searchByName("GAR")).thenReturn(List.of(employee));
        when(mapper.toResponse(employee)).thenReturn(response);

        assertEquals(List.of(response), service.searchByName("GAR"));
        verify(repository).searchByName("GAR");
    }

    @Test
    void rejectsBlankSearchInput() {
        assertThrows(EmployeeValidationException.class, () -> service.searchByName(" "));
        assertThrows(EmployeeValidationException.class, () -> service.searchByName(null));
        verify(repository, never()).searchByName(any());
    }

    private EmployeeResponse responseFor(Employee employee) {
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
                Instant.now(),
                employee.isActive()
        );
    }

    private Employee validEmployee() {
        Employee employee = new Employee();
        employee.setFirstName("Ana");
        employee.setSecondName("Maria");
        employee.setPaternalSurname("Garcia");
        employee.setMaternalSurname("Lopez");
        employee.setAge(30);
        employee.setSex(Sex.FEMALE);
        employee.setBirthDate(LocalDate.now().minusYears(30).minusDays(1));
        employee.setPosition("Developer");
        return employee;
    }
}
