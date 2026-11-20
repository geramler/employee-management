package com.company.employees.presentation.resource;

import com.company.employees.infrastructure.repository.EmployeeRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class EmployeeResourceTest {

    private static final DateTimeFormatter BIRTH_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Inject
    EmployeeRepository repository;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void createsListsSearchesAndDeletesEmployee() {
        Integer id = given()
                .contentType(ContentType.JSON)
                .body("[" + employeeRequest("Carlos", 35) + "]")
                .when().post("/employees")
                .then()
                .statusCode(201)
                .body("$", hasSize(1))
                .body("[0].id", notNullValue())
                .body("[0].createdAt", notNullValue())
                .extract().path("[0].id");

        given()
                .when().get("/employees/{id}", id)
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Carlos"));

        given()
                .queryParam("name", "Mart")
                .when().get("/employees/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(1));

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"position": "Principal Architect", "active": false, "secondName": null}
                        """)
                .when().put("/employees/{id}", id)
                .then()
                .statusCode(200)
                .body("position", equalTo("Principal Architect"))
                .body("secondName", equalTo(null))
                .body("active", equalTo(false));

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"age": 1}
                        """)
                .when().put("/employees/{id}", id)
                .then()
                .statusCode(400)
                .body("fieldErrors.age", notNullValue());

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"firstName": null, "age": null, "sex": null}
                        """)
                .when().put("/employees/{id}", id)
                .then()
                .statusCode(400)
                .body("fieldErrors.firstName", notNullValue())
                .body("fieldErrors.age", notNullValue())
                .body("fieldErrors.sex", notNullValue());

        given()
                .when().get("/employees/{id}", id)
                .then()
                .statusCode(200)
                .body("age", equalTo(35));

        given()
                .contentType(ContentType.JSON)
                .body("null")
                .when().put("/employees/{id}", id)
                .then()
                .statusCode(400)
                .body("timestamp", notNullValue())
                .body("status", equalTo(400))
                .body("fieldErrors", notNullValue());

        given()
                .when().delete("/employees/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when().get("/employees/{id}", id)
                .then()
                .statusCode(404)
                .body("timestamp", notNullValue())
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"))
                .body("path", equalTo("/employees/" + id));
    }

    @Test
    void rejectsInvalidCreatePayload() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        [{"firstName": "", "age": -1}]
                        """)
                .when().post("/employees")
                .then()
                .statusCode(400)
                .body("timestamp", notNullValue())
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("path", equalTo("/employees"))
                .body("fieldErrors", notNullValue());
    }

    @Test
    void acceptsSingleEmployeeObject() {
        given()
                .contentType(ContentType.JSON)
                .body(employeeRequest("Sofia", 30))
                .when().post("/employees")
                .then()
                .statusCode(201)
                .body("$", hasSize(1))
                .body("[0].firstName", equalTo("Sofia"));
    }

    @Test
    void batchCreateIsAtomicWhenAnEmployeeIsInvalid() {
        String invalidRequest = employeeRequest("Invalid", 34).replace(
                LocalDate.now().minusYears(34).minusDays(1).format(BIRTH_DATE_FORMAT),
                LocalDate.now().minusYears(35).minusDays(1).format(BIRTH_DATE_FORMAT)
        );
        given()
                .contentType(ContentType.JSON)
                .body("[" + employeeRequest("Valid", 35) + "," + invalidRequest + "]")
                .when().post("/employees")
                .then()
                .statusCode(400)
                .body("fieldErrors.age", notNullValue());

        given()
                .when().get("/employees")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    void createsValidBatchAndSupportsCaseInsensitiveSearchAcrossNames() {
        given()
                .contentType(ContentType.JSON)
                .body("[" + employeeRequest("Ana", 35) + "," + employeeRequest("Luis", 35) + "]")
                .when().post("/employees")
                .then()
                .statusCode(201)
                .body("$", hasSize(2))
                .body("[0].active", equalTo(true))
                .body("[0].birthDate", equalTo(LocalDate.now().minusYears(35).minusDays(1).format(BIRTH_DATE_FORMAT)))
                .body("[0].createdAt", notNullValue());

        given()
                .queryParam("name", "mAR")
                .when().get("/employees/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(2));

        given()
                .queryParam("name", "JOSE")
                .when().get("/employees/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(2));

        given()
                .queryParam("name", "PERE")
                .when().get("/employees/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(2));

        given()
                .queryParam("name", "no-such-name")
                .when().get("/employees/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));

        given()
                .queryParam("name", "_")
                .when().get("/employees/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));

        given()
                .queryParam("name", "%")
                .when().get("/employees/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    void defaultsActiveToTrueWhenOmitted() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        [{
                          "firstName": "Ana",
                          "paternalSurname": "Garcia",
                          "maternalSurname": "Lopez",
                          "age": 35,
                          "sex": "FEMALE",
                          "birthDate": "01-01-1991",
                          "position": "Developer"
                        }]
                        """)
                .when().post("/employees")
                .then()
                .statusCode(201)
                .body("[0].active", equalTo(true));
    }

    @Test
    void returns404ForMissingUpdateAndDelete() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"position": "Architect"}
                        """)
                .when().put("/employees/{id}", 999999)
                .then()
                .statusCode(404);

        given()
                .when().delete("/employees/{id}", 999999)
                .then()
                .statusCode(404);
    }

    @Test
    void returnsSanitizedErrorsForMalformedJsonAndBlankSearch() {
        given()
                .contentType(ContentType.JSON)
                .body("[{")
                .when().post("/employees")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("message", notNullValue())
                .body("path", equalTo("/employees"));

        given()
                .queryParam("name", " ")
                .when().get("/employees/search")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("message", notNullValue());
    }

    private String employeeRequest(String firstName, int age) {
        String birthDate = LocalDate.now().minusYears(age).minusDays(1).format(BIRTH_DATE_FORMAT);
        return """
                {
                  "firstName": "%s",
                  "secondName": "Jose",
                  "paternalSurname": "Martinez",
                  "maternalSurname": "Perez",
                  "age": %d,
                  "sex": "MALE",
                  "birthDate": "%s",
                  "position": "Architect",
                  "active": true
                }
                """.formatted(firstName, age, birthDate);
    }
}
