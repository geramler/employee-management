# Employee Management REST API

A Java 17 REST API built with Quarkus for employee management. The service uses a layered structure to separate the HTTP contract, application behavior, domain model, and persistence.

## Technology

- Java 17 and Maven
- Quarkus 3.40.1.2
- Quarkus REST with Jackson
- Hibernate ORM with Panache
- MySQL for runtime and H2 for tests
- Jakarta Bean Validation
- JUnit 5 and REST Assured, with Mockito support for tests
- SmallRye OpenAPI, Swagger UI, and SmallRye Health

## Project structure

```text
src/
  main/
    java/com/company/employees/
      application/
        dto/request/
        dto/response/
        mapper/
        service/
      domain/
        entity/
        enums/
        exception/
      infrastructure/
        config/
        repository/
      presentation/
        exception/
        resource/
    resources/application.yaml
  test/
    java/com/company/employees/
```

Resources handle HTTP concerns, services enforce business rules, repositories handle persistence, and DTOs keep persistence entities out of the API contract.

## Prerequisites

- JDK 17 or newer (the Maven build targets Java 17)
- Maven 3.9+
- MySQL for local runtime

## Configuration

The default runtime profile uses MySQL. Configure the following environment variables; credentials are not stored in the repository:

| Variable | Description |
| --- | --- |
| `DB_JDBC_URL` | MySQL JDBC URL, for example `jdbc:mysql://localhost:3306/employees` |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |

Development uses Hibernate schema updates. The production/default profile validates the schema rather than modifying it. Tests use an in-memory H2 database.

## Build and run

```bash
mvn test
export DB_JDBC_URL=jdbc:mysql://localhost:3306/employees
export DB_USERNAME=your-mysql-username
read -rsp "MySQL password: " DB_PASSWORD; echo
export DB_PASSWORD
mvn quarkus:dev
```

## API

Base URL: `http://localhost:8080`

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/employees` | List all employees |
| GET | `/employees/{id}` | Get one employee |
| POST | `/employees` | Create one or more employees |
| PUT | `/employees/{id}` | Partially or fully update an employee |
| DELETE | `/employees/{id}` | Delete an employee |
| GET | `/employees/search?name={name}` | Search names and surnames partially |

`POST /employees` accepts either one employee object or an array of employees. `PUT /employees/{id}` supports partial updates to match the assessment contract. Omitted fields are left unchanged; fields explicitly set to `null` can clear nullable values such as `secondName`. The resulting employee is validated, including consistency between `age` and `birthDate`. Batch creation is atomic: if any employee is invalid, none are created.

Errors use a JSON object containing `timestamp`, `status`, `error`, `message`, `path`, and `fieldErrors`. Unexpected server errors return a generic message without exposing implementation details.

Swagger UI is available at `/q/swagger-ui`. OpenAPI JSON is at `/q/openapi`, health is at `/q/health`.

## Docker

```bash
docker build -t employee-management .
docker run --rm -p 8080:8080 \
  -e DB_JDBC_URL=jdbc:mysql://host.docker.internal:3306/employees \
  -e DB_USERNAME=your-mysql-username \
  -e DB_PASSWORD=your-mysql-password \
  employee-management
```

The repository also includes a [Postman collection](postman/employee-management.postman_collection.json),
a static [OpenAPI contract](docs/openapi.yaml), and a [GitHub Actions CI workflow](.github/workflows/ci.yml).