package br.gov.es.openpmo.apis.organograma;

import java.util.Optional;

import static br.gov.es.openpmo.apis.organograma.OrganogramaApiImpl.accessToken;
import static br.gov.es.openpmo.apis.organograma.OrganogramaApiImpl.cacheUnidadeSigla;

public interface OrganogramaApi {

  static void clearCache() {
    cacheUnidadeSigla.clear();
    accessToken = null;
  }

  Optional<String> findSiglaByUnidade(String idUnidade);

}
