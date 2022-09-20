package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import br.gov.es.openpmo.repository.IsInContactBookOfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class IsInContactBookOfService {

  private final IsInContactBookOfRepository repository;

  @Autowired
  public IsInContactBookOfService(final IsInContactBookOfRepository repository) {
    this.repository = repository;
  }

  public IsInContactBookOf save(final IsInContactBookOf isInContactBookOf) {
    return this.repository.save(isInContactBookOf, 0);
  }

  public Optional<IsInContactBookOf> findContactInformationUsingPersonIdAndWorkpackId(
    final Long personId,
    final Long workpackId
  ) {
    return this.repository.findIsInContactBookOfUsingPersonIdAndWorkpackId(personId, workpackId);
  }

  @Cacheable("contactInformationByPersonAndOffice")
  public Optional<IsInContactBookOf> findContactInformationUsingPersonIdAndOffice(
    final Long personId,
    final Long officeId
  ) {
    return this.repository.findIsInContactBookOfByPersonIdAndOfficeId(personId, officeId);
  }

  public void saveAll(final Collection<IsInContactBookOf> contactInformation) {
    this.repository.saveAll(contactInformation);
  }

  public boolean existsByPersonIdAndOfficeId(
    final Long personId,
    final Long officeId
  ) {
    return this.repository.existsByPersonIdAndOfficeId(personId, officeId);
  }

}
