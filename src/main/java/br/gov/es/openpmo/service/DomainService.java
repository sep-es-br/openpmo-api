package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Domain;
import br.gov.es.openpmo.repository.DomainRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class DomainService {

    private final DomainRepository domainRepository;

    @Autowired
    public DomainService(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public List<Domain> findAll(Long idOffice) {
        return new ArrayList<>(domainRepository.findAll(idOffice));
    }

    public Domain save(Domain domain) {
        return domainRepository.save(domain);
    }

    public Domain update(Domain domain) {
        Domain domainBd = findById(domain.getId());
        domainBd.setName(domain.getName());
        domainBd.setFullName(domain.getFullName());
        return save(domainBd);
    }

    public Domain findById(Long id) {
        return domainRepository.findById(id).orElseThrow(() -> new NegocioException(ApplicationMessage.DOMAIN_NOT_FOUND));
    }
    
    public Domain findByIdWithLocalities(Long id) {
        return domainRepository.findByIdWithLocalities(id).orElseThrow(() -> new NegocioException(ApplicationMessage.DOMAIN_NOT_FOUND));
    }

    public void delete(Domain domain) {
        if (!CollectionUtils.isEmpty(domain.getLocalities())) {
            throw new NegocioException(ApplicationMessage.DOMAIN_DELETE_RELATIONSHIP_ERROR);
        }
        domainRepository.delete(domain);
    }
}
