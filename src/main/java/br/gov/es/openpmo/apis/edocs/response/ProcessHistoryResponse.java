package br.gov.es.openpmo.apis.edocs.response;

import br.gov.es.openpmo.apis.organograma.OrganogramaApi;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

public class ProcessHistoryResponse {

    private final LocalDateTime date;
    private final String name;
    private final String abbreviation;
    private final String descricaoTipo;
    private final OrganogramaApi api;

    public ProcessHistoryResponse(final JSONObject json, OrganogramaApi api) {
        this.api = api;
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
        Optional<String> maybeDescricaoTipo = this.getDescricaoTipo(json);
        if (!maybeDescricaoTipo.isPresent()) {
            return Optional.empty();
        }
        String descricaoTipo = maybeDescricaoTipo.get();
        String[] situacao1 = {"Despacho", "Reabertura", "Avocamento"};
        if (Arrays.asList(situacao1).contains(descricaoTipo)) {
            return buscarSiglaNoDestino(json);
        }
        String[] situacao2 = {"Autuacao", "Entranhamento", "Desentranhamento", "Encerramento"};
        if (Arrays.asList(situacao2).contains(descricaoTipo)) {
            return buscarSiglaNoPapel(json);
        }
        return Optional.empty();
    }

    private Optional<String> getSetor(final JSONObject json) {
        Optional<String> maybeDescricaoTipo = this.getDescricaoTipo(json);
        if (!maybeDescricaoTipo.isPresent()) {
            return Optional.empty();
        }
        String descricaoTipo = maybeDescricaoTipo.get();
        String[] situacao1 = {"Despacho", "Reabertura", "Avocamento"};
        if (Arrays.asList(situacao1).contains(descricaoTipo)) {
            return buscarSetorNoDestino(json);
        }
        String[] situacao2 = {"Autuacao", "Entranhamento", "Desentranhamento", "Encerramento"};
        if (Arrays.asList(situacao2).contains(descricaoTipo)) {
            return buscaSetorNoPapel(json);
        }
        return Optional.empty();
    }

    private Optional<String> buscarSiglaNoPapel(JSONObject json) {
        return Optional.of(json)
                .map(j -> j.optJSONObject("papel"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optJSONObject("organizacao"))
                .map(o -> o.optString("sigla"));
    }

    private Optional<String> buscaSetorNoPapel(JSONObject json) {
        return Optional.of(json)
                .map(j -> j.optJSONObject("papel"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optString("nome"));
    }

    private Optional<String> buscarSiglaNoDestino(JSONObject json) {
        Optional<JSONObject> destino = Optional.of(json)
                .map(j -> j.optJSONObject("destino"));

        Optional<String> destinoPapelSigla = destino
                .map(d -> d.optJSONObject("papel"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optString("id"))
                .flatMap(api::findSiglaByUnidade);

        if (destinoPapelSigla.isPresent()) {
            return destinoPapelSigla;
        }

        Optional<String> destinoGrupoSigla = destino
                .map(d -> d.optJSONObject("grupo"))
                .map(p -> p.optJSONObject("localizacao"))
                .map(s -> s.optString("id"))
                .flatMap(api::findSiglaByUnidade);

        if (destinoGrupoSigla.isPresent()) {
            return destinoGrupoSigla;
        }

        Optional<String> destinoSetorOrganizacaoSigla = destino
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optString("id"))
                .flatMap(api::findSiglaByUnidade);

        if (destinoSetorOrganizacaoSigla.isPresent()) {
            return destinoSetorOrganizacaoSigla;
        }

        return destino
                .map(s -> s.optJSONObject("organizacao"))
                .map(o -> o.optString("sigla"));
    }

    private Optional<String> buscarSetorNoDestino(JSONObject json) {
        Optional<JSONObject> destino = Optional.of(json)
                .map(j -> j.optJSONObject("destino"));

        Optional<String> destinoPapelSigla = destino
                .map(d -> d.optJSONObject("papel"))
                .map(p -> p.optJSONObject("setor"))
                .map(s -> s.optString("nome"));

        if (destinoPapelSigla.isPresent()) {
            return destinoPapelSigla;
        }

        Optional<String> destinoGrupoSigla = destino.map(d -> d.optJSONObject("grupo"))
                .map(s -> s.optString("nome"));

        if (destinoGrupoSigla.isPresent()) {
            return destinoGrupoSigla;
        }

        Optional<String> destinoSetor = destino.map(p -> p.optJSONObject("setor"))
                .map(s -> s.optString("nome"));

        if (destinoSetor.isPresent()) {
            return destinoSetor;
        }

        return destino.map(s -> s.optString("nome"));
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
