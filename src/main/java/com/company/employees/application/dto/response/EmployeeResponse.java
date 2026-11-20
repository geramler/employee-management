package com.company.employees.application.dto.response;

import com.company.employees.domain.enums.Sex;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String firstName,
        String secondName,
        String paternalSurname,
        String maternalSurname,
        Integer age,
        Sex sex,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate birthDate,
        String position,
        Instant createdAt,
        boolean active
) {
}
