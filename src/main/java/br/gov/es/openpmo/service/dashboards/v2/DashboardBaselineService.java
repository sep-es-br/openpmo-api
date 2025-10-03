package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.repository.dashboards.DashboardBaselineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DashboardBaselineService implements IDashboardBaselineService {

  private final DashboardBaselineRepository baselineRepository;

  @Autowired
  public DashboardBaselineService(final DashboardBaselineRepository baselineRepository) {
    this.baselineRepository = baselineRepository;
  }

  @Override
  public List<DashboardBaselineResponse> getBaselines(final Long workpackId) {
    return this.baselineRepository.findAllByWorkpackId(workpackId);
  }

    @Override
    public Optional<Baseline> findActiveBaseline(Long workpackId, String workpackType) {
        if(workpackId == null)
            return null;
        
        return baselineRepository
                .getActiveBaselineByWorkpack(workpackId, workpackType)
                .map(Baseline::getId)
                .flatMap(baselineRepository::findById);
    }
  
  
  
  

}
