package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.dto.administrator.AdministratorDto;
import br.gov.es.openpmo.dto.administrator.AdministratorStatusRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.PERSON_NOT_FOUND;

@Service
public class AdministratorService {

  private final PersonRepository personRepository;

  @Value("${app.login.server.name}")
  private String serverName;

  @Autowired
  public AdministratorService(final PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  public AdministratorDto updateAdministratorStatus(
    final AdministratorStatusRequest request,
    final Long personId
  ) {
    final boolean administrator = request.getAdministrator();

    final Person person = this.personRepository.findById(personId)
      .orElseThrow(() -> new NegocioException(PERSON_NOT_FOUND));

    person.setAdministrator(administrator);

    this.personRepository.setAdministratorStatus(person.getId(), person.getAdministrator());

    return new AdministratorDto(person, this.serverName);
  }

  public List<AdministratorDto> findAllAdministrators() {
    return this.personRepository.findAllAdministrators()
      .stream()
      .map(person -> new AdministratorDto(person, this.serverName))
      .collect(Collectors.toList());
  }

}
