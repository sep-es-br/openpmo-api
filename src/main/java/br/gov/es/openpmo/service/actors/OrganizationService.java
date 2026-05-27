package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.organization.OrganizationStoreDto;
import br.gov.es.openpmo.dto.organization.OrganizationUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.OrganizationOfficeRelationship;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.OrganizationRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllOrganizationUsingCustomFilter;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.organization.OrganizationSyncService;
import br.gov.es.openpmo.utils.ApplicationMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.gov.es.openpmo.utils.ApplicationMessage.CUSTOM_FILTER_NOT_FOUND;

@Service
public class OrganizationService {

  private final OrganizationRepository repository;
  private final OfficeService officeService;
  private final CustomFilterRepository customFilterRepository;
  private final FindAllOrganizationUsingCustomFilter findAllOrganization;
  private final AppProperties appProperties;

  private final OrganizationSyncService organizationSyncService;

  @Autowired
  public OrganizationService(
    final OrganizationRepository repository,
    final OfficeService officeService,
    final CustomFilterRepository customFilterRepository,
    final FindAllOrganizationUsingCustomFilter findAllOrganization,
    final AppProperties appProperties,
    final OrganizationSyncService organizationSyncService
  ) {
    this.repository = repository;
    this.officeService = officeService;
    this.customFilterRepository = customFilterRepository;
    this.findAllOrganization = findAllOrganization;
    this.appProperties = appProperties;
    this.organizationSyncService = organizationSyncService;
  }

  public List<Organization> findAll(final Long idOffice) {
    return this.repository.findByIdOffice(idOffice);
  }

  public List<Organization> findAll(
    final Long idOffice,
    final Long idFilter,
    final String term
  ) {
    if(idFilter == null) {
    	if (StringUtils.isBlank(term)) return this.repository.findByIdOffice(idOffice);
    	else return this.findAllByTerm(idOffice, term);
    }

    final CustomFilter filter = this.customFilterRepository
      .findById(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));

    final Map<String, Object> params = new HashMap<>();
    params.put("idOffice", idOffice);
    params.put("term", term);
    params.put("searchCutOffScore", appProperties.getSearchCutOffScore());
    
    if (StringUtils.isNotBlank(term)) filter.setSimilarityFilter(true);

    return this.findAllOrganization.execute(filter, params);
  }
  
  public List<Organization> findAllByTerm(final Long idOffice, final String term) {
	  return this.repository.findByIdOfficeAndByTerm(idOffice, term, this.appProperties.getSearchCutOffScore());
  }

  public Organization save(final Organization organization) {
    return this.repository.save(organization);
  }


  public void delete(final Organization organization) {
    this.repository.delete(organization);
  }

  public Organization buildOrganizationFromCreate (final OrganizationStoreDto organizationStoreDto) {
    
    final Organization organization = new Organization();
    organization.setName(organizationStoreDto.getName());
    organization.setFullName(organizationStoreDto.getFullName());
    organization.setSector(organizationStoreDto.getSector());
    
    final Office office = this.officeService.findById(organizationStoreDto.getIdOffice());
    
    final OrganizationOfficeRelationship relationship = new OrganizationOfficeRelationship();
    relationship.setOrganization(organization);
    relationship.setOffice(office);
    relationship.setPhoneNumber(organizationStoreDto.getPhoneNumber());
    relationship.setContactEmail(organizationStoreDto.getContactEmail());
    relationship.setAddress(organizationStoreDto.getAddress());
    relationship.setWebsite(organizationStoreDto.getWebsite());

    if (organization.getOrganizationOffices() == null) {
      organization.setOrganizationOffices(new ArrayList<>());
    }

    organization.getOrganizationOffices().add(relationship);
    
    return organization;
}

  public Organization getOrganization(
    final OrganizationUpdateDto organizationUpdateDto
) {

    final Organization organization =
        this.findById(organizationUpdateDto.getId());

    organization.setSector(organizationUpdateDto.getSector());
    organization.setName(organizationUpdateDto.getName());
    organization.setFullName(organizationUpdateDto.getFullName());

      OrganizationOfficeRelationship relationship =
              organization.getOrganizationOffices()
                      .stream()
                      .filter(r ->
                              r.getOffice() != null &&
                                      r.getOffice().getId()
                                              .equals(organizationUpdateDto.getIdOffice())
                      )
                      .findFirst()
                      .orElse(null);

      if (relationship == null) {

          relationship = new OrganizationOfficeRelationship();

          relationship.setOrganization(organization);

          relationship.setOffice(
                  officeService.findById(
                          organizationUpdateDto.getIdOffice()
                  )
          );

          organization.getOrganizationOffices()
                  .add(relationship);
      }

      relationship.setPhoneNumber(
              organizationUpdateDto.getPhoneNumber()
      );

      relationship.setContactEmail(
              organizationUpdateDto.getContactEmail()
      );

      relationship.setAddress(
              organizationUpdateDto.getAddress()
      );

      relationship.setWebsite(
              organizationUpdateDto.getWebsite()
      );

      return organization;
  }

  public Organization findById(final Long id) {
    return this.repository.findById(id)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.ORGANIZATION_NOT_FOUND));
  }

  public Organization findByIdAndOffice(final Long id, final Long idOffice) {
    return this.repository.findByIdAndIdOffice(id, idOffice)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.ORGANIZATION_NOT_FOUND));
  }

  public List<Organization> organizationInIsStakeholderIn(final Long idWorkpack) {
    return this.repository.findByIdWorkpackReturnDistinctOrganization(idWorkpack);
  }

  public boolean existsIntegratedOrganizationByName(String name){
      this.organizationSyncService.syncOrganizations();
      return this.repository.existsIntegratedOrganizationByName(name);
  }

}
