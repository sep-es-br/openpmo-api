package br.gov.es.openpmo.dto.person;

import com.fasterxml.jackson.annotation.JsonCreator;

public class NameRequest {

    private final String name;

    @JsonCreator
    public NameRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
