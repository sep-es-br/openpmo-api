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
        this.name = this.getSetor(json).orElse(null);
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

        Optional<String> destinoPapelSigla = destino.map(d -> d.optJSONObject("papel"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optJSONObject("organizacao"))
                .map(o -> o.optString("sigla"));

        if (destinoPapelSigla.isPresent()) {
            return destinoPapelSigla;
        }

        Optional<String> destinoGrupoSigla = destino.map(d -> d.optJSONObject("grupo"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optJSONObject("organizacao"))
                .map(o -> o.optString("sigla"));

        if (destinoGrupoSigla.isPresent()) {
            return destinoGrupoSigla;
        }

        Optional<String> destinoSigla = destino.map(p -> p.optJSONObject("setor"))
                .map(s -> s.optJSONObject("organizacao"))
                .map(o -> o.optString("sigla"));

        if (destinoSigla.isPresent()) {
            return destinoSigla;
        }

        Optional<String> papelSigla = optionalJson.map(d -> d.optJSONObject("papel"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optJSONObject("organizacao"))
                .map(o -> o.optString("sigla"));

        if (papelSigla.isPresent()) {
            return papelSigla;
        }

        return optionalJson.map(d -> d.optJSONObject("grupo"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optJSONObject("organizacao"))
                .map(o -> o.optString("sigla"));
    }

    private Optional<String> getSetor(JSONObject json) {
        Optional<JSONObject> optionalJson = Optional.of(json);

        Optional<String> localizacaoSetor = optionalJson
                .map(j -> j.optJSONObject("localizacao"))
                .map(l -> l.optString("nome"));

        if (localizacaoSetor.isPresent()) {
            return localizacaoSetor;
        }

        Optional<JSONObject> destino = optionalJson
                .map(j -> j.optJSONObject("destino"));

        Optional<String> destinoPapelSetor = destino.map(d -> d.optJSONObject("papel"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optString("nome"));

        if (destinoPapelSetor.isPresent()) {
            return destinoPapelSetor;
        }

        Optional<String> destinoGrupoSetor = destino.map(d -> d.optJSONObject("grupo"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optString("nome"));

        if (destinoGrupoSetor.isPresent()) {
            return destinoGrupoSetor;
        }

        Optional<String> destinoSetor = destino.map(p -> p.optJSONObject("setor"))
                .map(o -> o.optString("nome"));

        if (destinoSetor.isPresent()) {
            return destinoSetor;
        }

        Optional<String> papelSetor = optionalJson.map(d -> d.optJSONObject("papel"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optString("nome"));

        if (papelSetor.isPresent()) {
            return papelSetor;
        }

        return optionalJson.map(d -> d.optJSONObject("grupo"))
                .map(p -> p.optJSONObject("setor"))
                .map(o -> o.optString("nome"));
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
