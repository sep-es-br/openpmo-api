package br.gov.es.openpmo.service.organization;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.repository.OrganizationRepository;
import br.gov.es.openpmo.repository.WorkPlaceRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.pmo.organization_parser.pmo_base.model.IWorkLocationParser;
import br.gov.es.pmo.organization_parser.pmo_base.model.WorkLocationDto;
import br.gov.es.pmo.user_a_identify.model.IPublicIdentityProvider;
import br.gov.es.pmo.user_a_identify.model.PublicAgentAssignment;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityResult;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityStatus;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkPlaceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkPlaceService.class);

  private final WorkPlaceRepository repository;
  private final OrganizationRepository organizationRepository;
  private final ObjectProvider<IPublicIdentityProvider> identityProvider;
  private final ObjectProvider<IWorkLocationParser> workLocationParser;
  private final OrganizationTokenService organizationTokenService;
  private final TokenService tokenService;

  public WorkPlaceService(
    final WorkPlaceRepository repository,
    final OrganizationRepository organizationRepository,
    final ObjectProvider<IPublicIdentityProvider> identityProvider,
    final ObjectProvider<IWorkLocationParser> workLocationParser,
    final OrganizationTokenService organizationTokenService,
    final TokenService tokenService
  ) {
    this.repository = repository;
    this.organizationRepository = organizationRepository;
    this.identityProvider = identityProvider;
    this.workLocationParser = workLocationParser;
    this.organizationTokenService = organizationTokenService;
    this.tokenService = tokenService;
  }

  public Optional<Organization> findOrganization(
    final Long personId,
    final Long officeId
  ) {
    return this.repository.findOrganizationByPersonAndOffice(personId, officeId);
  }

  @Transactional
  public Optional<Organization> resolveForAuthenticatedUser(
    final String authorization,
    final Long officeId
  ) {
    final Long personId = this.tokenService.getUserId(authorization);
    final Optional<Organization> selected = this.findOrganization(personId, officeId);
    if(selected.isPresent()) {
      return selected;
    }

    final String externalSub = this.tokenService.getExternalIdentityKey(authorization);
    if(isBlank(externalSub)) {
      return Optional.empty();
    }

    final IPublicIdentityProvider identity = this.identityProvider.getIfAvailable();
    final IWorkLocationParser organization = this.workLocationParser.getIfAvailable();
    if(identity == null || organization == null) {
      return Optional.empty();
    }

    try {
      final Optional<Organization> localOrganization = this.resolveOrganizationByExternalSub(externalSub);
      localOrganization.ifPresent(value -> this.repository.replaceOrganization(
        personId,
        officeId,
        value.getId()
      ));
      return localOrganization;
    }
    catch(final RuntimeException exception) {
      LOGGER.warn("Unable to resolve the authenticated user's organization.", exception);
      return Optional.empty();
    }
  }

  public Optional<Organization> resolveOrganizationByExternalSub(final String externalSub) {
    if(isBlank(externalSub)) {
      return Optional.empty();
    }
    final IPublicIdentityProvider identity = this.identityProvider.getIfAvailable();
    if(identity == null) {
      return Optional.empty();
    }

    try {
      final PublicIdentityResult identityResult = identity.findPublicAgentBySub(externalSub);
      if(!PublicIdentityStatus.FOUND.equals(identityResult.getStatus()) || identityResult.getAssignments().isEmpty()) {
        return Optional.empty();
      }
      final PublicAgentAssignment assignment = identityResult.getAssignments().get(0);
      return this.resolveOrganizationByWorkLocationGuid(assignment.getWorkLocationGuid());
    }
    catch(final RuntimeException exception) {
      LOGGER.warn("Unable to resolve organization for external identity {}.", externalSub, exception);
      return Optional.empty();
    }
  }

  public Optional<Organization> resolveOrganizationByWorkLocationGuid(final String workLocationGuid) {
    if(isBlank(workLocationGuid)) {
      return Optional.empty();
    }
    final IWorkLocationParser organization = this.workLocationParser.getIfAvailable();
    if(organization == null) {
      return Optional.empty();
    }

    try {
      final String organizationToken = this.organizationTokenService.fetchSystemToken();
      final Optional<WorkLocationDto> workLocation = organization.findByGuid(
        workLocationGuid,
        organizationToken
      );
      if(!workLocation.isPresent() || isBlank(workLocation.get().getOrganizationGuid())) {
        return Optional.empty();
      }
      return this.organizationRepository.findByGuid(workLocation.get().getOrganizationGuid());
    }
    catch(final RuntimeException exception) {
      LOGGER.warn("Unable to resolve organization from work location {}.", workLocationGuid, exception);
      return Optional.empty();
    }
  }

  @Transactional
  public Organization selectOrganization(
    final Long personId,
    final Long officeId,
    final Long organizationId
  ) {
    final Organization organization = this.organizationRepository.findById(organizationId)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.ORGANIZATION_NOT_FOUND));

    // There must be only one structural WorkPlace for a person in an office.
    // Consolidate old duplicates before changing the selected organization.
    this.repository.removeDuplicateWorkPlaces(personId, officeId);
    this.repository.createWorkPlaceWithOrganizationIfMissing(personId, officeId, organizationId);

    final Long updated = this.repository.replaceOrganization(personId, officeId, organizationId);
    if(updated == null || updated == 0L) {
      throw new NegocioException(ApplicationMessage.CONTACT_DATA_NOT_FOUND);
    }
    return organization;
  }

  private static boolean isBlank(final String value) {
    return value == null || value.trim().isEmpty();
  }
}
