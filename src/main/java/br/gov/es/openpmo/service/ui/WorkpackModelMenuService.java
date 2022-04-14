package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.PlanModelMenuResponse;
import br.gov.es.openpmo.dto.menu.WorkpackModelMenuResponse;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.PlanModelRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class WorkpackModelMenuService {

    private final OfficeRepository officeRepository;

    private final PlanModelRepository planModelRepository;

    public WorkpackModelMenuService(OfficeRepository officeRepository, PlanModelRepository planModelRepository) {
        this.officeRepository = officeRepository;
        this.planModelRepository = planModelRepository;
    }

    public List<PlanModelMenuResponse> getResponses(Long idOffice) {
        List<PlanModelMenuResponse> planModelMenuResponses = new ArrayList<>();
        List<PlanModel> planModels = this.officeRepository.findAllPlanModelsByOfficeId(idOffice);

        for (PlanModel planModel : planModels) {
            PlanModelMenuResponse planModelMenuResponse = new PlanModelMenuResponse();

            planModelMenuResponse.setId(planModel.getId());
            planModelMenuResponse.setName(planModel.getName());
            planModelMenuResponse.setFullName(planModel.getFullName());

            List<WorkpackModel> workpackModels =
                    this.planModelRepository.findAllWorkpackModelsByPlanModelId(planModel.getId());

            if (workpackModels.isEmpty()) {
                continue;
            }

            Set<WorkpackModelMenuResponse> workpackModelMenuResponses = new HashSet<>();

            for (WorkpackModel workpackModel : workpackModels) {
                WorkpackModelMenuResponse response = getResponse(planModel, workpackModel);
                workpackModelMenuResponses.add(response);
            }

            planModelMenuResponse.setWorkpackModels(workpackModelMenuResponses);
            planModelMenuResponses.add(planModelMenuResponse);
        }

        return planModelMenuResponses;
    }

    private WorkpackModelMenuResponse getResponse(PlanModel planModel, WorkpackModel workpackModel) {
        WorkpackModelMenuResponse item = new WorkpackModelMenuResponse();

        item.setId(workpackModel.getId());
        item.setIdPlanModel(planModel.getId());
        item.setName(workpackModel.getModelName());
        item.setFontIcon(workpackModel.getFontIcon());

        Set<WorkpackModelMenuResponse> children = new HashSet<>();

        if (workpackModel.getChildren() == null) {
            item.setChildren(null);
            return item;
        }

        for (WorkpackModel child : workpackModel.getChildren()) {
            WorkpackModelMenuResponse response = getResponse(planModel, child);
            children.add(response);
        }

        item.setChildren(children);
        return item;
    }

}
