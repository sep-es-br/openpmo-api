package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.planmodel.PlanModelUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.PlanModel;
import br.gov.es.openpmo.model.WorkpackModel;
import br.gov.es.openpmo.repository.PlanModelRepository;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class PlanModelService {

    private final PlanModelRepository planModelRepository;
    private final WorkpackModelRepository workpackModelRepository;

    @Autowired
    public PlanModelService(PlanModelRepository planModelRepository, WorkpackModelRepository workpackModelRepository) {
        this.planModelRepository = planModelRepository;
        this.workpackModelRepository = workpackModelRepository;
    }

    public List<PlanModel> findAll() {
        List<PlanModel> planModels = new ArrayList<>();
        planModelRepository.findAll().forEach(planModels::add);
        return planModels;
    }

    public List<PlanModel> findAllInOffice(Long id) {
        return planModelRepository.findAllInOffice(id);
    }

    public PlanModel save(PlanModel planModel) {
        return planModelRepository.save(planModel);
    }

    public PlanModel findById(Long id) {
        return planModelRepository.findById(id).orElseThrow(
            () -> new NegocioException(ApplicationMessage.PLANMODEL_NOT_FOUND));
    }

    public void delete(PlanModel planModel) {
        Set<WorkpackModel> workpackModels = new HashSet<>(
            workpackModelRepository.findAllByIdPlanModel(planModel.getId()));
        if (!workpackModels.isEmpty()) {
            throw new NegocioException(ApplicationMessage.PLANMODEL_DELETE_REALATIONSHIP_ERROR);
        }
        planModelRepository.delete(planModel);
    }

    public PlanModel getPlanModel(PlanModelUpdateDto planModelUpdateDto) {
        PlanModel planModel = findById(planModelUpdateDto.getId());
        planModel.setName(planModelUpdateDto.getName());
        planModel.setFullName(planModelUpdateDto.getFullName());
        return planModel;
    }
}

