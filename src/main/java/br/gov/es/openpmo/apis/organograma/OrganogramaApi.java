package br.gov.es.openpmo.apis.organograma;

import java.util.Optional;

public interface OrganogramaApi {

    Optional<String> findSiglaByUnidade(String idUnidade);

}
