package br.gov.es.openpmo.apis.edocs.enums;

import br.gov.es.openpmo.apis.edocs.interfaces.ModelFunction;
import br.gov.es.openpmo.apis.organograma.OrganogramaApi;
import org.json.JSONObject;

import java.util.Optional;

public enum ModelType {

  A(ModelType::getSetorModeloA, ModelType::getSiglaModeloA),
  B(ModelType::getSetorModeloB, ModelType::getSiglaModeloB),
  C(ModelType::getSetorModeloC, ModelType::getSiglaModeloC);

  private final ModelFunction setor;
  private final ModelFunction sigla;

  ModelType(
    final ModelFunction setor,
    final ModelFunction sigla
  ) {
    this.setor = setor;
    this.sigla = sigla;
  }

  private static Optional<String> getSetorModeloA(
    final OrganogramaApi api,
    final JSONObject json
  ) {
    final Optional<String> maybeLocalizacao = Optional.of(json)
      .map(p -> p.optJSONObject("localizacao"))
      .map(s -> s.optString("nome"));

    if(maybeLocalizacao.isPresent()) {
      return maybeLocalizacao;
    }

    return Optional.of(json)
      .map(j -> j.optJSONObject("papel"))
      .map(p -> p.optJSONObject("setor"))
      .map(s -> s.optString("nome"));
  }

  private static Optional<String> getSetorModeloB(
    final OrganogramaApi api,
    final JSONObject json
  ) {
    return Optional.of(json)
      .map(j -> j.optJSONObject("papel"))
      .map(p -> p.optJSONObject("setor"))
      .map(s -> s.optString("nome"));
  }

  private static Optional<String> getSetorModeloC(
    final OrganogramaApi api,
    final JSONObject json
  ) {
    final Optional<JSONObject> destino = Optional.of(json)
      .map(j -> j.optJSONObject("destino"));

    final Optional<String> destinoPapelSigla = destino
      .map(d -> d.optJSONObject("papel"))
      .map(p -> p.optJSONObject("setor"))
      .map(s -> s.optString("nome"));

    if(destinoPapelSigla.isPresent()) {
      return destinoPapelSigla;
    }

    final Optional<String> destinoGrupoSigla = destino.map(d -> d.optJSONObject("grupo"))
      .map(s -> s.optString("nome"));

    if(destinoGrupoSigla.isPresent()) {
      return destinoGrupoSigla;
    }

    final Optional<String> destinoSetor = destino.map(p -> p.optJSONObject("setor"))
      .map(s -> s.optString("nome"));

    if(destinoSetor.isPresent()) {
      return destinoSetor;
    }

    return destino.map(s -> s.optString("nome"));
  }

  private static Optional<String> getSiglaModeloA(
    final OrganogramaApi api,
    final JSONObject json
  ) {
    final Optional<String> maybeLocalizacao = Optional.of(json)
      .map(p -> p.optJSONObject("localizacao"))
      .map(s -> s.optString("id"))
      .flatMap(api::findSiglaByUnidade);

    if(maybeLocalizacao.isPresent()) {
      return maybeLocalizacao;
    }

    return Optional.of(json)
      .map(j -> j.optJSONObject("papel"))
      .map(p -> p.optJSONObject("setor"))
      .map(s -> s.optJSONObject("organizacao"))
      .map(o -> o.optString("sigla"));
  }

  private static Optional<String> getSiglaModeloB(
    final OrganogramaApi api,
    final JSONObject json
  ) {
    return Optional.of(json)
      .map(j -> j.optJSONObject("papel"))
      .map(p -> p.optJSONObject("setor"))
      .map(s -> s.optJSONObject("organizacao"))
      .map(o -> o.optString("sigla"));
  }

  private static Optional<String> getSiglaModeloC(
    final OrganogramaApi api,
    final JSONObject json
  ) {
    final Optional<JSONObject> destino = Optional.of(json)
      .map(j -> j.optJSONObject("destino"));

    final Optional<String> destinoPapelSigla = destino
      .map(d -> d.optJSONObject("papel"))
      .map(p -> p.optJSONObject("setor"))
      .map(s -> s.optString("id"))
      .flatMap(api::findSiglaByUnidade);

    if(destinoPapelSigla.isPresent()) {
      return destinoPapelSigla;
    }

    final Optional<String> destinoGrupoSigla = destino
      .map(d -> d.optJSONObject("grupo"))
      .map(p -> p.optJSONObject("localizacao"))
      .map(s -> s.optString("id"))
      .flatMap(api::findSiglaByUnidade);

    if(destinoGrupoSigla.isPresent()) {
      return destinoGrupoSigla;
    }

    final Optional<String> destinoSetorOrganizacaoSigla = destino
      .map(p -> p.optJSONObject("setor"))
      .map(s -> s.optString("id"))
      .flatMap(api::findSiglaByUnidade);

    if(destinoSetorOrganizacaoSigla.isPresent()) {
      return destinoSetorOrganizacaoSigla;
    }

    return destino
      .map(s -> s.optJSONObject("organizacao"))
      .map(o -> o.optString("sigla"));
  }

  public Optional<String> getSetor(
    final OrganogramaApi api,
    final JSONObject json
  ) {
    return this.setor.apply(api, json);
  }

  public Optional<String> getSigla(
    final OrganogramaApi api,
    final JSONObject json
  ) {
    return this.sigla.apply(api, json);
  }

}
