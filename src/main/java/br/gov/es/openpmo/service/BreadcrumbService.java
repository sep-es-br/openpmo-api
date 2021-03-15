package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.menu.BreadcrumbDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.PropertyModelDto;
import br.gov.es.openpmo.model.Domain;
import br.gov.es.openpmo.model.Locality;
import br.gov.es.openpmo.model.Office;
import br.gov.es.openpmo.model.Plan;
import br.gov.es.openpmo.model.PlanModel;
import br.gov.es.openpmo.model.Property;
import br.gov.es.openpmo.model.Workpack;
import br.gov.es.openpmo.model.WorkpackModel;

@Service
public class BreadcrumbService {

    private final WorkpackService workpackService;
    private final WorkpackModelService workpackModelService;
    private final LocalityService localityService;
    private final PlanService planService;
    private final PlanModelService planModelService;
    private final OfficeService officeService;

    @Autowired
    public BreadcrumbService(WorkpackService workpackService, WorkpackModelService workpackModelService,
                             LocalityService localityService, PlanService planService,
                             OfficeService officeService, PlanModelService planModelService) {
        this.workpackService = workpackService;
        this.workpackModelService = workpackModelService;
        this.localityService = localityService;
        this.planService = planService;
        this.officeService = officeService;
        this.planModelService = planModelService;
    }

    public List<BreadcrumbDto> localities(Long id) {
        List<BreadcrumbDto> breadcrumbs = new ArrayList<>(0);
        Locality locality = localityService.findByIdWithParent(id);
        breadcrumbs.add(new BreadcrumbDto(locality.getId(), locality.getName(), locality.getFullName(), locality.getType().name()));
        if (locality.getParent() != null) {
            addParentBreadcrumbDto(locality.getParent(), breadcrumbs);
        }
        // addDomainBreadcrumbDto(breadcrumbs, locality.getDomain());
        Collections.reverse(breadcrumbs);
        return breadcrumbs;
    }

    public List<BreadcrumbDto> workpacks(Long id) {
        List<BreadcrumbDto> breadcrumbs = new ArrayList<>(0);
        Workpack workpack = workpackService.findByIdWithParent(id);
        breadcrumbs.add(getBreadcrumbDto(workpack));
        if (workpack.getParent() != null) {
            addParentBreadcrumbDto(workpack.getParent(), breadcrumbs);
        }
        addOfficeAndPlanBreadcrumbDto(breadcrumbs, workpack.getIdPlan());
        Collections.reverse(breadcrumbs);
        return breadcrumbs;
    }

    private void addDomainBreadcrumbDto(List<BreadcrumbDto> breadcrumbs, Domain domain) {
        Office office = domain.getOffice();
        BreadcrumbDto breadcrumbDomainDto = new BreadcrumbDto(domain.getId(), domain.getName(), domain.getFullName(), "domain");
        BreadcrumbDto breadcrumbDomainListDto = new BreadcrumbDto(office.getId(), office.getName(), office.getFullName(), "domains");
        breadcrumbs.add(breadcrumbDomainDto);
        breadcrumbs.add(breadcrumbDomainListDto);
    }

    public List<BreadcrumbDto> models(Long id) {
        List<BreadcrumbDto> breadcrumbs = new ArrayList<>(0);
        WorkpackModel workpackModel = workpackModelService.findByIdWithParents(id);
        breadcrumbs.add(new BreadcrumbDto(workpackModel.getId(), workpackModel.getModelName(), workpackModel.getModelNameInPlural(), workpackModel.getClass().getSimpleName()));
        if (workpackModel.getParent() != null) {
            addParentBreadcrumbDto(workpackModel.getParent(), breadcrumbs);
        }
        addPlanModelBreadcrumbDto(breadcrumbs, workpackModel.getIdPlanModel());
        Collections.reverse(breadcrumbs);
        return breadcrumbs;
    }

    private void addOfficeAndPlanBreadcrumbDto(List<BreadcrumbDto> breadcrumbs, Long idPlan) {
        Plan plan = planService.findById(idPlan);
        Office office = plan.getOffice();
        BreadcrumbDto breadcrumbPlanDto = new BreadcrumbDto(plan.getId(), plan.getName(), plan.getFullName(), "plan");
        BreadcrumbDto breadcrumbOfficeDto = new BreadcrumbDto(office.getId(), office.getName(), office.getFullName(), "office");
        breadcrumbs.add(breadcrumbPlanDto);
        breadcrumbs.add(breadcrumbOfficeDto);
    }

    private void addPlanModelBreadcrumbDto(List<BreadcrumbDto> breadcrumbs, Long idPlanModel) {
        PlanModel planModel = planModelService.findById(idPlanModel);
        Office office = planModel.getOffice();
        BreadcrumbDto breadcrumbPlanModelDto = new BreadcrumbDto(planModel.getId(), planModel.getName(), planModel.getFullName(), "strategy");
        BreadcrumbDto breadcrumbPlanModelListDto = new BreadcrumbDto(office.getId(), office.getName(), office.getFullName(), "strategies");
        breadcrumbs.add(breadcrumbPlanModelDto);
        breadcrumbs.add(breadcrumbPlanModelListDto);
    }

    private void addParentBreadcrumbDto(Locality locality, List<BreadcrumbDto> breadcrumbs) {
        breadcrumbs.add(new BreadcrumbDto(locality.getId(), locality.getName(), locality.getFullName(), locality.getType().name()));
        if (locality.getParent() != null) {
            addParentBreadcrumbDto(locality.getParent(), breadcrumbs);
        }
    }

    private void addParentBreadcrumbDto(Workpack workpack, List<BreadcrumbDto> breadcrumbs) {
        breadcrumbs.add(getBreadcrumbDto(workpack));
        if (workpack.getParent() != null) {
            addParentBreadcrumbDto(workpack.getParent(), breadcrumbs);
        }
    }

    private void addParentBreadcrumbDto(WorkpackModel workpackModel, List<BreadcrumbDto> breadcrumbs) {
        breadcrumbs.add(new BreadcrumbDto(workpackModel.getId(), workpackModel.getModelName(), workpackModel.getModelNameInPlural(),workpackModel.getClass().getSimpleName()));
        if (workpackModel.getParent() != null) {
            addParentBreadcrumbDto(workpackModel.getParent(), breadcrumbs);
        }
    }

    private BreadcrumbDto getBreadcrumbDto(Workpack workpack) {
        BreadcrumbDto breadcrumbDto = new BreadcrumbDto(workpack.getId(), "", "", workpack.getClass().getSimpleName());
        breadcrumbDto.setType(workpack.getClass().getSimpleName());
        WorkpackDetailDto detailDto = workpackService.getWorkpackDetailDto(workpack);
        if (detailDto.getModel() != null && detailDto.getModel().getProperties() != null) {
            breadcrumbDto.setName(getValueProperty(workpack,detailDto.getModel().getProperties(), detailDto.getProperties(), "name"));
            breadcrumbDto.setFullName(getValueProperty(workpack,detailDto.getModel().getProperties(), detailDto.getProperties(), "fullName"));
        }
        return breadcrumbDto;
    }

    private String getValueProperty(Workpack workpack, List<? extends PropertyModelDto> propertyModels, List<? extends PropertyDto>  properties, String name) {
        PropertyModelDto propertyModelName = propertyModels.stream().filter(
            pm -> pm.getName().equals(name)).findFirst().orElse(null);
        PropertyDto propertyName = properties.stream().filter(
            pn -> propertyModelName != null && pn.getIdPropertyModel().equals(
                propertyModelName.getId())).findFirst().orElse(null);
        Property property = workpack.getProperties().stream().filter(
            p -> propertyName != null && p.getId().equals(propertyName.getId())).findFirst().orElse(null);
        if (property != null) {
            return (String) workpackService.getValueProperty(property);
        }
        return null;
    }
}
