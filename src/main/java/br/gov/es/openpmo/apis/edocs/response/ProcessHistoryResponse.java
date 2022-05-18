package br.gov.es.openpmo.apis.edocs.response;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ProcessHistoryResponse {

    private final LocalDateTime date;
    private final String name;
    private final String abbreviation;
    private final String descricaoTipo;

    public ProcessHistoryResponse(final JSONObject json) {
        this.date = this.getDate(json).orElse(null);
        this.name = this.getDestinoNome(json).orElse(null);
        this.abbreviation = this.getSigla(json).orElse(null);
        this.descricaoTipo = this.getDescricaoTipo(json).orElse(null);
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

    private Optional<String> getSigla(final JSONObject json) {
        Optional<JSONObject> optionalJson = Optional.of(json);

        Optional<String> localizacaoSigla = optionalJson
                .map(j -> j.optJSONObject("localizacao"))
                .map(l -> l.optString("sigla"));

        if (localizacaoSigla.isPresent()) {
            return localizacaoSigla;
        }

        Optional<JSONObject> destino = optionalJson
                .map(j -> j.optJSONObject("destino"));

        Optional<String> papelSigla = destino.map(d -> d.optJSONObject("papel"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optJSONObject("organizacao"))
                .map(o -> o.optString("sigla"));

        if (papelSigla.isPresent()) {
            return papelSigla;
        }

        Optional<String> grupoSigla = destino.map(d -> d.optJSONObject("grupo"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optJSONObject("organizacao"))
                .map(o -> o.optString("sigla"));

        if (grupoSigla.isPresent()) {
            return grupoSigla;
        }

        return destino.map(p -> p.optJSONObject("setor"))
                .map(s -> s.optJSONObject("organizacao"))
                .map(o -> o.optString("sigla"));
    }

    private Optional<String> getDestinoNome(JSONObject json) {
        return Optional.of(json)
                .map(j -> j.optJSONObject("destino"))
                .map(d -> d.optString("nome"));
    }

    private Optional<String> getDescricaoTipo(final JSONObject json) {
        return Optional.of(json)
                .map(j -> j.optString("descricaoTipo"));
    }

    private Optional<LocalDateTime> getDate(final JSONObject json) {
        return Optional.of(json)
                .map(j -> j.optString("dataHora"))
                .map(d -> LocalDateTime.parse(d, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

}
