package br.gov.es.openpmo.service.procurements;

import br.gov.es.pmo.procurement_core.model.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProcurementProviderService {
    private final ObjectProvider<IProcurementProvider> provider;

    public ProcurementProviderService(ObjectProvider<IProcurementProvider> p) {
        provider = p;
    }

    private IProcurementProvider get() {
        return provider.getIfAvailable();
    }

    public List<Long> years() {
        return get() == null ? Collections.emptyList() : get().getYears();
    }

    public List<ProcurementOrganizationDto> organizations(Long y) {
        return get() == null ? Collections.emptyList() : get().getOrganizations(y);
    }

    public List<br.gov.es.pmo.procurement_core.model.ProcurementDto> processes(Long y, String id, String name) {
        return get() == null ? Collections.emptyList()
                : get().getProcurements(y, new ProcurementOrganizationDto(id, name));
    }

    public br.gov.es.pmo.procurement_core.model.ProcurementDto detail(Long id) {
        return get() == null ? null : get().getProcurement(id);
    }

    public boolean isAvailable() {
        return get() != null;
    }
}
