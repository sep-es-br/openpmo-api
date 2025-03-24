package br.gov.es.openpmo.service.workpack;

import java.util.List;

import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.menu.WorkpackMenuResultDto;
import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.repository.PlanRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;

@Service
public class PortifolioService {

    private final WorkpackRepository workpackRepository;
    private final PlanRepository planRepository;

    public PortifolioService(WorkpackRepository workpackRepository, PlanRepository planRepository) {
        this.workpackRepository = workpackRepository;
        this.planRepository = planRepository;
    }

    public List<WorkpackResultDto> findAllMenuCustomByIdPlan(Long idPlan) {
        return workpackRepository.findAllMenuCustomByIdPlan(idPlan);
    }

    public List<WorkpackResultDto> findAllMenuCustomByIdPlanWithSort(Long idPlan) {
        return workpackRepository.findAllMenuCustomByIdPlanWithSort(idPlan);
    }

    public String getHashCodeMenuCustomByIdPlan(Long idPlan) {
        List<String> list = workpackRepository.getHashCodeMenuCustomByIdPlan(idPlan);
        return "" + list.hashCode();
    }

    public List<Long> findAllPlanIds() {
        return planRepository.findAllIds();
    }

}
