package br.gov.es.openpmo.apis.organograma;

import java.util.Optional;

import static br.gov.es.openpmo.apis.organograma.OrganogramaApiImpl.accessToken;
import static br.gov.es.openpmo.apis.organograma.OrganogramaApiImpl.cacheUnidadeSigla;

public interface OrganogramaApi {

    Optional<String> findSiglaByUnidade(String idUnidade);

    static void clearCache() {
        cacheUnidadeSigla.clear();
        accessToken = null;
    }
}
