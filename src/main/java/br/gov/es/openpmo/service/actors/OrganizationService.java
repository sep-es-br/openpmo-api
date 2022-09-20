package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.dto.organization.OrganizationUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.OrganizationRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllOrganizationUsingCustomFilter;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

  @Autowired
  public OrganizationService(
    final OrganizationRepository repository,
    final OfficeService officeService,
    final CustomFilterRepository customFilterRepository,
    final FindAllOrganizationUsingCustomFilter findAllOrganization
  ) {
    this.repository = repository;
    this.officeService = officeService;
    this.customFilterRepository = customFilterRepository;
    this.findAllOrganization = findAllOrganization;
  }

  public List<Organization> findAll(final Long idOffice) {
    return this.repository.findByIdOffice(idOffice);
  }

  public List<Organization> findAll(
    final Long idOffice,
    final Long idFilter
  ) {
    if(idFilter == null) {
      return this.repository.findByIdOffice(idOffice);
    }

    final CustomFilter filter = this.customFilterRepository
      .findById(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));

    final Map<String, Object> params = new HashMap<>();
    params.put("idOffice", idOffice);

    return this.findAllOrganization.execute(filter, params);
  }

  public Organization save(final Organization organization) {
    return this.repository.save(organization);
  }

  public Organization save(
    final Organization organization,
    final Long idOffice
  ) {
    organization.setOffice(this.getOfficeById(idOffice));
    return this.repository.save(organization);
  }

  private Office getOfficeById(final Long idOffice) {
    return this.officeService.findById(idOffice);
  }

  public void delete(final Organization organization) {
    this.repository.delete(organization);
  }

  public Organization getOrganization(final OrganizationUpdateDto organizationUpdateDto) {
    final Organization organization = this.findById(organizationUpdateDto.getId());
    organization.setSector(organizationUpdateDto.getSector());
    organization.setWebsite(organizationUpdateDto.getWebsite());
    organization.setAddress(organizationUpdateDto.getAddress());
    organization.setContactEmail(organizationUpdateDto.getContactEmail());
    organization.setName(organizationUpdateDto.getName());
    organization.setFullName(organizationUpdateDto.getFullName());
    organization.setPhoneNumber(organizationUpdateDto.getPhoneNumber());
    return organization;
  }

  public Organization findById(final Long id) {
    return this.repository.findById(id)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.ORGANIZATION_NOT_FOUND));
  }

  public List<Organization> organizationInIsStakeholderIn(final Long idWorkpack) {
    return this.repository.findByIdWorkpackReturnDistinctOrganization(idWorkpack);
  }

}
