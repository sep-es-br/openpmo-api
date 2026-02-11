package br.gov.es.openpmo.apis.organograma;

import java.util.Optional;

public interface OrganogramaApi {

  void clearCache();

  Optional<String> findSiglaByUnidade(String idUnidade);

}
