package br.gov.es.openpmo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.organization.OrganizationUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Office;
import br.gov.es.openpmo.model.Organization;
import br.gov.es.openpmo.repository.OrganizationRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class OrganizationService {

    private final OrganizationRepository repository;
    private final OfficeService officeService;

    @Autowired
    public OrganizationService(OrganizationRepository repository, OfficeService officeService) {
        this.repository = repository;
        this.officeService = officeService;
    }

    public List<Organization> findAll(Long idOffice) {
        return repository.findByIdOffice(idOffice);
    }

    public Organization save(Organization organization) {
        return repository.save(organization);
    }

    public Organization save(Organization organization, Long idOffice) {
        organization.setOffice(getOfficeById(idOffice));
        return repository.save(organization);
    }

    public Organization findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NegocioException(ApplicationMessage.ORGANIZATION_NOT_FOUND));
    }

    public void delete(Organization organization) {
        repository.delete(organization);
    }

    public Organization getOrganization(OrganizationUpdateDto organizationUpdateDto) {
        Organization organization = findById(organizationUpdateDto.getId());
        organization.setSector(organizationUpdateDto.getSector());
        organization.setWebsite(organizationUpdateDto.getWebsite());
        organization.setAddress(organizationUpdateDto.getAddress());
        organization.setEmail(organizationUpdateDto.getEmail());
        organization.setContactEmail(organizationUpdateDto.getContactEmail());
        organization.setName(organizationUpdateDto.getName());
        organization.setFullName(organizationUpdateDto.getFullName());
        organization.setPhoneNumber(organizationUpdateDto.getPhoneNumber());
        return organization;
    }

    public List<Organization> organizationInIsStakeholderIn(Long idWorkpack) {
        return repository.findByIdWorkpackReturnDistinctOrganization(idWorkpack);
    }

    private Office getOfficeById(Long idOffice) {
        return officeService.findById(idOffice);
    }

}
