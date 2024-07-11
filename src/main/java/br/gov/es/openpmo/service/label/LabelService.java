package br.gov.es.openpmo.service.label;

import br.gov.es.openpmo.repository.BaselineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabelService {

    private final BaselineRepository baselineRepository;

    @Autowired
    public LabelService(
            final BaselineRepository baselineRepository) {
        this.baselineRepository = baselineRepository;
    }

    public Boolean getLabel(final Long workpackId) {
        return this.baselineRepository.workpackHasSnapshotOrProjectWithBaseline(workpackId);
    }
}
