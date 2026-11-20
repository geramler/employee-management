package com.company.employees.application.dto.request;

import com.company.employees.domain.enums.Sex;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpdateEmployeeRequest {

    @Size(max = 100)
    public String firstName;

    @JsonIgnore
    private boolean firstNameSpecified;

    @JsonSetter("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.firstNameSpecified = true;
    }

    @JsonIgnore
    public boolean isFirstNameSpecified() {
        return firstNameSpecified;
    }

    @Size(max = 100)
    public String secondName;

    @JsonIgnore
    private boolean secondNameSpecified;

    @JsonSetter("secondName")
    public void setSecondName(String secondName) {
        this.secondName = secondName;
        this.secondNameSpecified = true;
    }

    @JsonIgnore
    public boolean isSecondNameSpecified() {
        return secondNameSpecified;
    }

    @Size(max = 100)
    public String paternalSurname;

    @JsonIgnore
    private boolean paternalSurnameSpecified;

    @JsonSetter("paternalSurname")
    public void setPaternalSurname(String paternalSurname) {
        this.paternalSurname = paternalSurname;
        this.paternalSurnameSpecified = true;
    }

    @JsonIgnore
    public boolean isPaternalSurnameSpecified() {
        return paternalSurnameSpecified;
    }

    @Size(max = 100)
    public String maternalSurname;

    @JsonIgnore
    private boolean maternalSurnameSpecified;

    @JsonSetter("maternalSurname")
    public void setMaternalSurname(String maternalSurname) {
        this.maternalSurname = maternalSurname;
        this.maternalSurnameSpecified = true;
    }

    @JsonIgnore
    public boolean isMaternalSurnameSpecified() {
        return maternalSurnameSpecified;
    }

    @Positive
    public Integer age;

    @JsonIgnore
    private boolean ageSpecified;

    @JsonSetter("age")
    public void setAge(Integer age) {
        this.age = age;
        this.ageSpecified = true;
    }

    @JsonIgnore
    public boolean isAgeSpecified() {
        return ageSpecified;
    }

    public Sex sex;

    @JsonIgnore
    private boolean sexSpecified;

    @JsonSetter("sex")
    public void setSex(Sex sex) {
        this.sex = sex;
        this.sexSpecified = true;
    }

    @JsonIgnore
    public boolean isSexSpecified() {
        return sexSpecified;
    }

    @Past
    @JsonFormat(pattern = "dd-MM-yyyy")
    public LocalDate birthDate;

    @JsonIgnore
    private boolean birthDateSpecified;

    @JsonSetter("birthDate")
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        this.birthDateSpecified = true;
    }

    @JsonIgnore
    public boolean isBirthDateSpecified() {
        return birthDateSpecified;
    }

    @Size(max = 150)
    public String position;

    @JsonIgnore
    private boolean positionSpecified;

    @JsonSetter("position")
    public void setPosition(String position) {
        this.position = position;
        this.positionSpecified = true;
    }

    @JsonIgnore
    public boolean isPositionSpecified() {
        return positionSpecified;
    }

    public Boolean active;

    @JsonIgnore
    private boolean activeSpecified;

    @JsonSetter("active")
    public void setActive(Boolean active) {
        this.active = active;
        this.activeSpecified = true;
    }

    @JsonIgnore
    public boolean isActiveSpecified() {
        return activeSpecified;
    }
}
