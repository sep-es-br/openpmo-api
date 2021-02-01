package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.gov.es.openpmo.dto.domain.LocalityStoreDto;
import br.gov.es.openpmo.dto.domain.LocalityUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Locality;
import br.gov.es.openpmo.repository.LocalityRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class LocalityService {

    private final LocalityRepository localityRepository;
    private final DomainService domainService;
    private final ModelMapper modelMapper;

    @Autowired
    public LocalityService(LocalityRepository localityRepository, ModelMapper modelMapper,
                           DomainService domainService) {
        this.localityRepository = localityRepository;
        this.modelMapper = modelMapper;
        this.domainService = domainService;
    }

    public List<Locality> findAll(Long idDomain) {
        return new ArrayList<>(localityRepository.findAllByDomain(idDomain));
    }

    public List<Locality> findAllFirstLevel(Long idDomain) {
        return new ArrayList<>(localityRepository.findAllByDomainFirstLevel(idDomain));
    }

    public List<Locality> findAllByDomainProperties(Long idDomain) {
        return new ArrayList<>(localityRepository.findAllByDomainProperties(idDomain));
    }

    public Locality save(Locality locality) {
        validateLocalityType(locality);
        return localityRepository.save(locality);
    }

    public Locality update(Locality locality) {
        Locality localityUpdate = findById(locality.getId());
        localityUpdate.setType(locality.getType());
        localityUpdate.setName(locality.getName());
        localityUpdate.setFullName(locality.getFullName());
        localityUpdate.setLatitude(locality.getLatitude());
        localityUpdate.setLongitude(locality.getLongitude());
        validateLocalityType(locality);
        return localityRepository.save(localityUpdate);
    }

    public Locality findById(Long id) {
        return localityRepository.findById(id).orElseThrow(
            () -> new NegocioException(ApplicationMessage.LOCALITY_NOT_FOUND));
    }

    public void delete(Locality locality) {
        if (!CollectionUtils.isEmpty(locality.getChildren())) {
            throw new NegocioException(ApplicationMessage.LOCALITY_DELETE_RELATIONSHIP_ERROR);
        }
        localityRepository.delete(locality);
    }

    public Locality getLocality(LocalityStoreDto localityParamDto) {
        Locality locality = modelMapper.map(localityParamDto, Locality.class);
        locality.setDomain(domainService.findById(localityParamDto.getIdDomain()));
        if (localityParamDto.getIdParent() != null) {
            locality.setParent(findById(localityParamDto.getIdParent()));
        }
        return locality;
    }

    public Locality getLocality(LocalityUpdateDto localityUpdateDto) {
        Locality locality = findById(localityUpdateDto.getId());
        locality.setName(localityUpdateDto.getName());
        locality.setFullName(localityUpdateDto.getFullName());
        locality.setType(localityUpdateDto.getType());
        locality.setLatitude(localityUpdateDto.getLatitude());
        locality.setLongitude(localityUpdateDto.getLongitude());
        return locality;
    }

    private void validateLocalityType(Locality locality) {
        if (locality.getParent() == null && !CollectionUtils.isEmpty(locality.getDomain().getLocalities())) {
            Collection<Locality> localities = localityRepository.findAllByDomainFirstLevel(locality.getDomain().getId());
            if (!localities.iterator().next().getType().equals(locality.getType())) {
                throw new NegocioException(ApplicationMessage.LOCALITY_TYPE_ERROR);
            }
        }
        if (locality.getParent() != null) {
            Locality parent = findById(locality.getParent().getId());
            if (!CollectionUtils.isEmpty(parent.getChildren())) {
                if (locality.getId() != null && parent.getChildren().stream().anyMatch(
                    l -> l.getId() != null && !l.getId().equals(locality.getId()) && !l.getType().equals(
                        locality.getType()))) {
                    throw new NegocioException(ApplicationMessage.LOCALITY_TYPE_ERROR);
                }

                if (locality.getId() == null && !locality.getType().equals(
                    parent.getChildren().iterator().next().getType())) {
                    throw new NegocioException(ApplicationMessage.LOCALITY_TYPE_ERROR);
                }
            }

        }
    }

}
