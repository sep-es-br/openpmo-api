package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.dto.person.CitizenByNameQuery;
import br.gov.es.openpmo.dto.person.CitizenDto;
import br.gov.es.openpmo.dto.person.CitizenDtoBuilder;
import br.gov.es.openpmo.dto.person.RoleResource;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.service.organization.WorkPlaceService;
import br.gov.es.pmo.user_a_identify.model.IPublicIdentityProvider;
import br.gov.es.pmo.user_a_identify.model.OrganizationInfo;
import br.gov.es.pmo.user_a_identify.model.PublicAgentAssignment;
import br.gov.es.pmo.user_a_identify.model.PublicAgentSearchResult;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityResult;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityStatus;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.CITIZEN_NOT_FOUND;

@Service
public class CitizenService {

  private final PersonService personService;
  private final IsInContactBookOfService contactService;
  private final ObjectProvider<IPublicIdentityProvider> publicIdentityProvider;
  private final WorkPlaceService workPlaceService;

  public CitizenService(
    final PersonService personService,
    final IsInContactBookOfService contactService,
    final ObjectProvider<IPublicIdentityProvider> publicIdentityProvider,
    final WorkPlaceService workPlaceService
  ) {
    this.personService = personService;
    this.contactService = contactService;
    this.publicIdentityProvider = publicIdentityProvider;
    this.workPlaceService = workPlaceService;
  }

  @Transactional
  public List<CitizenByNameQuery> findPersonByName(
    final String name,
    final Long idPerson
  ) {
    final PublicAgentSearchResult result = this.getPublicIdentityProvider().findPublicAgentsByName(name);

    if(PublicIdentityStatus.UNAVAILABLE.equals(result.getStatus())) {
      throw new NegocioException("Não foi possível consultar os agentes públicos.");
    }
    return result.getAgents().stream()
      .map(agent -> new CitizenByNameQuery(agent.getName(), agent.getSub()))
      .collect(Collectors.toList());
  }

  public CitizenDto findPersonByCpf(
    final String cpf,
    final Long idOffice,
    final Long idPerson
  ) {
    final PublicIdentityResult result = this.getPublicIdentityProvider().findByCpf(cpf);
    this.ensureIdentityFound(result);
    return this.toCitizenDto(result, idOffice);
  }

  public CitizenDto findCitizenBySub(
    final String sub,
    final Long idOffice,
    final Long idPerson
  ) {
    final PublicIdentityResult result = this.getPublicIdentityProvider().findPublicAgentBySub(sub);
    this.ensureIdentityFound(result);
    return this.toCitizenDto(result, idOffice);
  }

  private IPublicIdentityProvider getPublicIdentityProvider() {
    final IPublicIdentityProvider provider = this.publicIdentityProvider.getIfAvailable();
    if(provider == null) {
      throw new NegocioException("Plugin de validação de identidade não disponível.");
    }
    return provider;
  }

  private void ensureIdentityFound(final PublicIdentityResult result) {
    if(PublicIdentityStatus.NOT_FOUND.equals(result.getStatus())) {
      throw new NegocioException(CITIZEN_NOT_FOUND);
    }
    if(!PublicIdentityStatus.FOUND.equals(result.getStatus())) {
      throw new NegocioException("Não foi possível validar o usuário no Acesso Cidadão.");
    }
  }

  private CitizenDto toCitizenDto(
    final PublicIdentityResult identity,
    final Long idOffice
  ) {
    final List<RoleResource> roles = identity.getAssignments().stream()
      .map(this::toRoleResource)
      .collect(Collectors.toList());
    final Optional<Person> maybePerson = this.personService.findByKey(identity.getSub());
    final CitizenDtoBuilder builder = CitizenDtoBuilder.aCitizenDto()
      .withKey(identity.getSub())
      .withName(identity.getName())
      .withFullName(identity.getName())
      .withEmail(identity.getEmail())
      .withContactEmail(identity.getCorporateEmail())
      .withIsUser(maybePerson.isPresent())
      .withRoles(roles);

    maybePerson.ifPresent(person -> {
      builder.withId(person.getId());
      builder.withAdministrator(person.getAdministrator());
      if(person.getName() != null) builder.withName(person.getName());
      if(person.getFullName() != null) builder.withFullName(person.getFullName());
      if(idOffice != null) {
        this.contactService.findContactInformationUsingPersonIdAndOffice(
          person.getId(),
          idOffice
        ).ifPresent(contact -> builder
          .withAddress(contact.getAddress())
          .withPhoneNumber(contact.getPhoneNumber())
        );
      }
    });
    return builder.build();
  }

  private RoleResource toRoleResource(final PublicAgentAssignment assignment) {
    final OrganizationInfo organizationInfo = assignment.getOrganization();
    final String organization = organizationInfo == null
      ? this.workPlaceService.resolveOrganizationByWorkLocationGuid(assignment.getWorkLocationGuid())
        .map(this::organizationName)
        .orElse(assignment.getWorkLocationGuid())
      : firstNotBlank(
        organizationInfo.getAbbreviation(),
        organizationInfo.getTradeName(),
        organizationInfo.getCorporateName(),
        organizationInfo.getGuid()
      );
    return new RoleResource(assignment.getRoleName(), organization);
  }

  private String organizationName(final Organization organization) {
    return firstNotBlank(
      organization.getName(),
      organization.getFullName(),
      organization.getGuid()
    );
  }

  private static String firstNotBlank(final String... values) {
    for(final String value : values) {
      if(value != null && !value.trim().isEmpty()) {
        return value;
      }
    }
    return null;
  }
}
