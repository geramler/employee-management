package com.company.employees.application.service;

import com.company.employees.application.dto.request.CreateEmployeeRequest;
import com.company.employees.application.dto.request.UpdateEmployeeRequest;
import com.company.employees.application.dto.response.EmployeeResponse;
import com.company.employees.domain.enums.Sex;
import com.company.employees.domain.exception.EmployeeValidationException;
import com.company.employees.infrastructure.repository.EmployeeRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class EmployeeServiceTest {

    @Inject
    EmployeeService service;

    @Inject
    EmployeeRepository repository;

    @BeforeEach
    @Transactional
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @Transactional
    void createAndFindEmployee() {
        CreateEmployeeRequest request = validRequest();
        List<EmployeeResponse> created = service.create(List.of(request));

        assertEquals(1, created.size());
        assertNotNull(created.get(0).id());
        assertNotNull(created.get(0).createdAt());
        assertEquals("Ana", created.get(0).firstName());
        assertEquals("Ana", service.findById(created.get(0).id()).firstName());
    }

    @Test
    @Transactional
    void partiallyUpdatesEmployee() {
        EmployeeResponse created = service.create(List.of(validRequest())).get(0);
        UpdateEmployeeRequest update = new UpdateEmployeeRequest();
        update.position = "Lead Developer";
        update.active = false;

        EmployeeResponse updated = service.update(created.id(), update);

        assertEquals("Lead Developer", updated.position());
        assertFalse(updated.active());
        assertEquals("Ana", updated.firstName());
    }

    @Test
    @Transactional
    void searchesByPartialNameAcrossNameFields() {
        service.create(List.of(validRequest()));

        List<EmployeeResponse> result = service.searchByName("gar");

        assertEquals(1, result.size());
        assertEquals("Garcia", result.get(0).paternalSurname());
    }

    @Test
    void rejectsAgeThatDoesNotMatchBirthDate() {
        CreateEmployeeRequest request = validRequest();
        request.age = 29;

        assertThrows(EmployeeValidationException.class, () -> service.create(List.of(request)));
        assertEquals(0, repository.count());
    }

    @Test
    @Transactional
    void createsValidBatchAndDefaultsActiveState() {
        CreateEmployeeRequest first = validRequest();
        first.firstName = "Ana";
        first.active = null;
        CreateEmployeeRequest second = validRequest();
        second.firstName = "Luis";
        second.birthDate = LocalDate.now().minusYears(30).minusDays(1);

        List<EmployeeResponse> created = service.create(List.of(first, second));

        assertEquals(2, created.size());
        assertTrue(created.stream().allMatch(employee -> employee.active()));
        assertEquals(2, repository.count());
    }

    private CreateEmployeeRequest validRequest() {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.firstName = "Ana";
        request.secondName = "Maria";
        request.paternalSurname = "Garcia";
        request.maternalSurname = "Lopez";
        request.age = 30;
        request.sex = Sex.FEMALE;
        request.birthDate = LocalDate.now().minusYears(30).minusDays(1);
        request.position = "Developer";
        request.active = true;
        return request;
    }
}
