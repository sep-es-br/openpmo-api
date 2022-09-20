package br.gov.es.openpmo.apis.edocs.enums;

import br.gov.es.openpmo.apis.organograma.OrganogramaApi;
import org.json.JSONObject;

import java.util.Optional;

public enum Model {

  AUTUACAO("Autuacao", ModelType.A),
  ENCERRAMENTO("Encerramento", ModelType.A),
  EDICAO("Edicao", ModelType.A),

  ENTRANHAMENTO("Entranhamento", ModelType.B),
  DESENTRANHAMENTO("Desentranhamento", ModelType.B),
  AJUSTE_CUSTODIA("AjusteCustodia", ModelType.B),

  DESPACHO("Despacho", ModelType.C),
  REABERTURA("Reabertura", ModelType.C),
  AVOCAMENTO("Avocamento", ModelType.C);

  private final String nome;
  private final ModelType modelType;

  Model(
    final String nome,
    final ModelType modelType
  ) {
    this.nome = nome;
    this.modelType = modelType;
  }

  public static Optional<String> getSetor(
    final String nome,
    final OrganogramaApi api,
    final JSONObject json
  ) {
    final Model[] values = Model.values();
    for(final Model model : values) {
      if(model.nome.equals(nome)) {
        return model.modelType.getSetor(api, json);
      }
    }
    return Optional.empty();
  }

  public static Optional<String> getSigla(
    final String nome,
    final OrganogramaApi api,
    final JSONObject json
  ) {
    final Model[] values = Model.values();
    for(final Model model : values) {
      if(model.nome.equals(nome)) {
        return model.modelType.getSigla(api, json);
      }
    }
    return Optional.empty();
  }

}
