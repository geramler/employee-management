package com.company.employees.application.dto.request;

import com.company.employees.domain.enums.Sex;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateEmployeeRequest {

    @NotBlank
    @Size(max = 100)
    public String firstName;

    @Size(max = 100)
    public String secondName;

    @NotBlank
    @Size(max = 100)
    public String paternalSurname;

    @NotBlank
    @Size(max = 100)
    public String maternalSurname;

    @NotNull
    @Positive
    public Integer age;

    @NotNull
    public Sex sex;

    @NotNull
    @Past
    @JsonFormat(pattern = "dd-MM-yyyy")
    public LocalDate birthDate;

    @NotBlank
    @Size(max = 150)
    public String position;

    public Boolean active = true;
}
