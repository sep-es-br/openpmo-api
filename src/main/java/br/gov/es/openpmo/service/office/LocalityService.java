package br.gov.es.openpmo.service.office;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.domain.LocalityStoreDto;
import br.gov.es.openpmo.dto.domain.LocalityUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.office.Domain;
import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.LocalityRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllLocalityUsingCustomFilter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.CUSTOM_FILTER_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.DOMAIN_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.LOCALITY_DELETE_RELATIONSHIP_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.LOCALITY_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.LOCALITY_ROOT_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.LOCALITY_ROOT_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.LOCALITY_TYPE_ERROR;

@Service
public class LocalityService {

  private final LocalityRepository localityRepository;

  private final DomainService domainService;

  private final ModelMapper modelMapper;

  private final CustomFilterRepository customFilterRepository;

  private final FindAllLocalityUsingCustomFilter findAllLocality;

  private final AppProperties appProperties;

  @Autowired
  public LocalityService(
    final LocalityRepository localityRepository,
    final ModelMapper modelMapper,
    final DomainService domainService,
    final CustomFilterRepository customFilterRepository,
    final FindAllLocalityUsingCustomFilter findAllLocality,
    final AppProperties appProperties
  ) {
    this.localityRepository = localityRepository;
    this.modelMapper = modelMapper;
    this.domainService = domainService;
    this.customFilterRepository = customFilterRepository;
    this.findAllLocality = findAllLocality;
    this.appProperties = appProperties;
  }

  public List<Locality> findAll(final Long idDomain) {
    return new ArrayList<>(this.localityRepository.findAllByDomain(idDomain));
  }

  public List<Locality> findAllFirstLevel(
    final Long idDomain,
    final Long idFilter,
    final String term
  ) {
    if (idFilter == null) {
      return this.findAllFirstLevel(
        idDomain,
        term
      );
    }
    final CustomFilter filter = this.customFilterRepository
      .findById(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));
    final Map<String, Object> params = new HashMap<>();
    params.put(
      "idDomain",
      idDomain
    );
    params.put(
      "term",
      term
    );
    params.put(
      "searchCutOffScore",
      appProperties.getSearchCutOffScore()
    );
    return this.findAllLocality.execute(
      filter,
      params
    );
  }

  private List<Locality> findAllFirstLevel(
    final Long idDomain,
    final String term
  ) {
    final Double searchCutOffScore = appProperties.getSearchCutOffScore();
    return new ArrayList<>(this.localityRepository.findAllByDomainFirstLevel(
      idDomain,
      term,
      searchCutOffScore
    ));
  }

  public List<Locality> findAllByDomainProperties(final Long idDomain) {
    return new ArrayList<>(this.localityRepository.findAllByDomainProperties(idDomain));
  }

  public Locality save(final Locality locality) {
    this.validateLocalityType(locality);
    return this.localityRepository.save(locality);
  }

  private void validateLocalityType(final Locality locality) {
    if (locality.getParent() == null && locality.getDomain() != null) {
      final Locality localityRoot = this.localityRepository.findLocalityRootFromDomain(locality.getDomainId())
        .orElseThrow(() -> new NegocioException(LOCALITY_ROOT_NOT_FOUND));
      final boolean notLocalityRoot = !localityRoot.getId().equals(locality.getId());
      if (notLocalityRoot) {
        throw new NegocioException(LOCALITY_ROOT_ERROR);
      }
    }
    if (locality.getParent() != null) {
      final Locality parent = this.findById(locality.getParent().getId());
      if (parent.getChildren() != null && !(parent.getChildren()).isEmpty()) {
        if (locality.getId() != null && parent.getChildren().stream().anyMatch(l -> l.getId() != null && !l.getId().equals(
          locality.getId()) && !l.getType().equals(locality.getType()))) {
          throw new NegocioException(LOCALITY_TYPE_ERROR);
        }
        if (locality.getId() == null && !locality.getType().equals(parent.getChildren().iterator().next().getType())) {
          throw new NegocioException(LOCALITY_TYPE_ERROR);
        }
      }
    }
  }

  public Locality findById(final Long id) {
    return this.localityRepository.findById(id)
      .orElseThrow(() -> new NegocioException(LOCALITY_NOT_FOUND));
  }

  public Locality findById(
    final Long id,
    final Long idFilter,
    final String term
  ) {
    final Locality locality = this.findById(id);
    if (idFilter == null) {
      return locality;
    }
    final Domain domain = this.localityRepository.findDomainById(id)
      .orElseThrow(() -> new NegocioException(DOMAIN_NOT_FOUND));
    final CustomFilter filter = this.customFilterRepository
      .findById(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));
    final Map<String, Object> params = new HashMap<>();
    params.put(
      "idDomain",
      domain.getId()
    );
    params.put(
      "term",
      term
    );
    params.put(
      "searchCutOffScore",
      appProperties.getSearchCutOffScore()
    );
    final List<Locality> localities = this.findAllLocality.execute(
      filter,
      params
    );
    final List<Long> localitiesId = localities.stream().map(Locality::getId).collect(Collectors.toList());
    this.filterChildren(
      locality,
      localitiesId
    );
    return locality;
  }

  void filterChildren(
    final Locality locality,
    final List<Long> localitiesId
  ) {
    final Set<Locality> children = locality.getChildren();
    if (children == null || children.isEmpty()) {
      return;
    }
    final Set<Locality> localities = new HashSet<>();
    for (final Locality child : children) {
      if (localitiesId.contains(child.getId())) {
        this.filterChildren(
          child,
          localitiesId
        );
        localities.add(child);
      }
    }
    locality.setChildren(localities);
  }

  public Locality update(final Locality locality) {
    final Locality localityUpdate = this.findById(locality.getId());
    localityUpdate.update(locality);
    this.validateLocalityType(locality);
    return this.localityRepository.save(localityUpdate);
  }

  public Locality findByIdWithParent(final Long id) {
    return this.localityRepository.findByIdWithParent(id)
      .orElseThrow(() -> new NegocioException(LOCALITY_NOT_FOUND));
  }

  public void delete(final Locality locality) {
    if (locality.getChildren() != null && !(locality.getChildren()).isEmpty()) {
      throw new NegocioException(LOCALITY_DELETE_RELATIONSHIP_ERROR);
    }
    this.localityRepository.delete(locality);
  }

  public Locality getLocality(final LocalityStoreDto localityParamDto) {
    final Locality locality = this.modelMapper.map(
      localityParamDto,
      Locality.class
    );
    locality.setDomain(this.domainService.findById(localityParamDto.getIdDomain()));
    if (localityParamDto.getIdParent() != null) {
      locality.setParent(this.findById(localityParamDto.getIdParent()));
    }
    return locality;
  }

  public Locality getLocality(final LocalityUpdateDto localityUpdateDto) {
    final Locality locality = this.findById(localityUpdateDto.getId());
    locality.setName(localityUpdateDto.getName());
    locality.setFullName(localityUpdateDto.getFullName());
    locality.setType(localityUpdateDto.getType());
    locality.setLatitude(localityUpdateDto.getLatitude());
    locality.setLongitude(localityUpdateDto.getLongitude());
    return locality;
  }

}
