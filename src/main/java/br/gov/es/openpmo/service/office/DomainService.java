package br.gov.es.openpmo.service.office;

import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.office.Domain;
import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.DomainRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllDomainUsingCustomFilter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.gov.es.openpmo.utils.ApplicationMessage.CUSTOM_FILTER_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.DOMAIN_DELETE_RELATIONSHIP_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.DOMAIN_NOT_FOUND;

@Service
public class DomainService {

  private final DomainRepository domainRepository;
  private final OfficeService officeService;
  private final CustomFilterRepository customFilterRepository;
  private final FindAllDomainUsingCustomFilter findAllDomain;
  private final ModelMapper modelMapper;

  @Autowired
  public DomainService(
    final DomainRepository domainRepository,
    final CustomFilterRepository customFilterRepository,
    final OfficeService officeService,
    final FindAllDomainUsingCustomFilter findAllDomain,
    final ModelMapper modelMapper
  ) {
    this.domainRepository = domainRepository;
    this.officeService = officeService;
    this.customFilterRepository = customFilterRepository;
    this.findAllDomain = findAllDomain;
    this.modelMapper = modelMapper;
  }

  public List<Domain> findAll(
    final Long idOffice,
    final Long idFilter
  ) {

    if(idFilter == null) {
      return this.findAll(idOffice);
    }

    final CustomFilter filter = this.customFilterRepository
      .findById(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));

    final Map<String, Object> params = new HashMap<>();
    params.put("idOffice", idOffice);

    return this.findAllDomain.execute(filter, params);
  }

  public List<Domain> findAll(final Long idOffice) {
    return new ArrayList<>(this.domainRepository.findAll(idOffice));
  }

  public Domain save(final DomainStoreDto domainDto) {
    final Domain domain = this.extractDomainFrom(domainDto);

    final Locality localityRoot = this.modelMapper.map(
      domainDto.getLocalityRoot(),
      Locality.class
    );
    domain.setLocalityRoot(localityRoot);
    domain.setOffice(this.officeService.findById(domainDto.getIdOffice()));
    return this.domainRepository.save(domain);
  }

  private Domain extractDomainFrom(final DomainStoreDto domainDto) {
    return this.modelMapper.map(
      domainDto,
      Domain.class
    );
  }

  public Domain update(final Domain domain) {
    final Domain domainBd = this.findById(domain.getId());
    domainBd.update(domain);
    return this.domainRepository.save(domainBd);
  }

  public Domain findById(final Long id) {
    return this.domainRepository
      .findById(id)
      .orElseThrow(() -> new NegocioException(DOMAIN_NOT_FOUND));
  }

  public Domain findByIdWithLocalities(final Long id) {
    return this.domainRepository.findByIdWithLocalities(id)
      .orElseThrow(() -> new NegocioException(DOMAIN_NOT_FOUND));
  }

  public void delete(final Domain domain) {
    if(!(domain.getLocalities()).isEmpty()) {
      throw new NegocioException(DOMAIN_DELETE_RELATIONSHIP_ERROR);
    }
    this.domainRepository.delete(domain);
  }

}
