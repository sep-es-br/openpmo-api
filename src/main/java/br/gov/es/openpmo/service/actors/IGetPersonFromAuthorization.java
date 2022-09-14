package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.model.actors.Person;

@FunctionalInterface
public interface IGetPersonFromAuthorization {

  PersonDataResponse execute(String authorization);


  interface PersonDataResponse {

    Person getPerson();

    String getEmail();

    String getKey();

  }

}
