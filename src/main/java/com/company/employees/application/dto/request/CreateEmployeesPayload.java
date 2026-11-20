package com.company.employees.application.dto.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = CreateEmployeesPayload.Deserializer.class)
public record CreateEmployeesPayload(
        @NotEmpty List<@NotNull @Valid CreateEmployeeRequest> employees
) {
    public static class Deserializer extends StdDeserializer<CreateEmployeesPayload> {

        public Deserializer() {
            super(CreateEmployeesPayload.class);
        }

        @Override
        public CreateEmployeesPayload deserialize(
                JsonParser parser,
                DeserializationContext context
        ) throws IOException {
            ObjectMapper mapper = (ObjectMapper) parser.getCodec();
            JsonNode payload = mapper.readTree(parser);

            if (payload.isObject()) {
                return new CreateEmployeesPayload(
                        List.of(mapper.treeToValue(payload, CreateEmployeeRequest.class))
                );
            }

            if (payload.isArray()) {
                List<CreateEmployeeRequest> employees = new ArrayList<>();
                for (JsonNode node : payload) {
                    if (!node.isObject()) {
                        throw context.weirdStringException(
                                node.asText(),
                                CreateEmployeeRequest.class,
                                "Each employee must be a JSON object."
                        );
                    }
                    employees.add(mapper.treeToValue(node, CreateEmployeeRequest.class));
                }
                return new CreateEmployeesPayload(employees);
            }

            throw context.weirdStringException(
                    payload.asText(),
                    CreateEmployeesPayload.class,
                    "Request must be an employee object or array."
            );
        }
    }
}
