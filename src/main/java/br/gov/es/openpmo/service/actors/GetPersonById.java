package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static br.gov.es.openpmo.utils.ApplicationMessage.PERSON_NOT_FOUND;

@Component
public class GetPersonById {

  private final PersonRepository personRepository;


  @Autowired
  public GetPersonById(final PersonRepository personRepository) {
    this.personRepository = personRepository;
  }


  public Person execute(final Long id) {
    Objects.requireNonNull(id);
    return this.personRepository.findById(id)
      .orElseThrow(() -> new NegocioException(PERSON_NOT_FOUND));
  }

}
