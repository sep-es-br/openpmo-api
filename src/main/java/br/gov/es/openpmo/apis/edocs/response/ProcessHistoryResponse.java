package br.gov.es.openpmo.apis.edocs.response;

import br.gov.es.openpmo.apis.edocs.enums.Model;
import br.gov.es.openpmo.apis.organograma.OrganogramaApi;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ProcessHistoryResponse {

    private final LocalDateTime date;
    private final String name;
    private final String abbreviation;
    private final String descricaoTipo;
    private final OrganogramaApi api;

    public ProcessHistoryResponse(final JSONObject json, OrganogramaApi api) {
        this.api = api;

        this.descricaoTipo = Optional.of(json)
                .map(j -> j.optString("descricaoTipo"))
                .orElse(null);

        this.date = Optional.of(json)
                .map(j -> j.optString("dataHora"))
                .map(d -> LocalDateTime.parse(d, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .orElse(null);

        this.name = Optional.ofNullable(this.descricaoTipo)
                .flatMap(descricao -> Model.getSetor(descricao, this.api, json))
                .orElse(null);

        this.abbreviation = Optional.ofNullable(this.descricaoTipo)
                .flatMap(descricao -> Model.getSigla(descricao, this.api, json))
                .orElse(null);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getDescricaoTipo() {
        return descricaoTipo;
    }

}
