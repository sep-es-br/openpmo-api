package br.gov.es.openpmo.service.obligations;

import br.gov.es.pmo.obligation_core.model.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ObligationProviderService {
    private final ObjectProvider<IObligationProvider> provider;

    public ObligationProviderService(ObjectProvider<IObligationProvider> p) {
        provider = p;
    }

    private IObligationProvider get() {
        return provider.getIfAvailable();
    }

    public List<Long> years() {
        return get() == null ? Collections.emptyList() : get().getYears();
    }

    public List<ObligationManagementUnitDto> units(Long y) {
        return get() == null ? Collections.emptyList() : get().getManagementUnits(y);
    }

    public List<br.gov.es.pmo.obligation_core.model.ObligationDto> processes(Long y, String code) {
        return get() == null ? Collections.emptyList()
                : get().getObligations(y, new ObligationManagementUnitDto(code, null));
    }

    public br.gov.es.pmo.obligation_core.model.ObligationDto detail(String id, String code) {
        return get() == null ? null : get().getObligation(id, code);
    }

    public boolean isAvailable() {
        return get() != null;
    }
}
