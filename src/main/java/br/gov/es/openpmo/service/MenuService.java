package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.menu.MenuOfficeDto;
import br.gov.es.openpmo.dto.menu.PlanModelMenuDto;
import br.gov.es.openpmo.dto.menu.WorkpackMenuDto;
import br.gov.es.openpmo.dto.menu.WorkpackModelMenuDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.PropertyModelDto;
import br.gov.es.openpmo.model.Office;
import br.gov.es.openpmo.model.Plan;
import br.gov.es.openpmo.model.PlanModel;
import br.gov.es.openpmo.model.Property;
import br.gov.es.openpmo.model.Workpack;
import br.gov.es.openpmo.model.WorkpackModel;

@Service
public class MenuService {

    private final WorkpackService workpackService;
    private final WorkpackModelService workpackModelService;
    private final PlanService planService;
    private final OfficeService officeService;
    private final ModelMapper modelMapper;
    private final PlanModelService planModelService;

    @Autowired
    public MenuService(WorkpackService workpackService, WorkpackModelService workpackModelService,
                       PlanService planService, OfficeService officeService, ModelMapper modelMapper,
                       PlanModelService planModelService) {
        this.workpackModelService = workpackModelService;
        this.workpackService = workpackService;
        this.planService = planService;
        this.officeService = officeService;
        this.modelMapper = modelMapper;
        this.planModelService = planModelService;
    }

    public List<WorkpackMenuDto> findAllPortfolio(Long idOffice) {
        List<WorkpackMenuDto> menus = new ArrayList<>(0);
        Office office = officeService.findById(idOffice);
        List<Plan> plans = planService.findAllInOffice(office.getId());
        plans.forEach(plan -> {
            Set<Workpack> workpacks = workpackService.findAllByPlanWithProperties(plan.getId());
            menus.addAll(getWorkpackMenuDto(workpacks));

        });
        return menus;
    }

    public List<MenuOfficeDto> findAllOffice() {
        List<MenuOfficeDto> menus = new ArrayList<>(0);
        List<Office> offices = officeService.findAll();
        offices.forEach(office -> {
            MenuOfficeDto item = modelMapper.map(office, MenuOfficeDto.class);
            List<PlanModel> planModels = planModelService.findAllInOffice(office.getId());
            planModels.forEach(plan -> {
                PlanModelMenuDto planModel = modelMapper.map(plan, PlanModelMenuDto.class);
                Set<WorkpackModel> workpackModels = workpackModelService.findAllByIdPlanModelWithChildren(plan.getId());
                planModel.getModels().addAll(getWorkpackModelMenuDto(workpackModels));
                item.getPlanModels().add(planModel);
            });
            menus.add(item);
        });
        return menus;
    }

    private Set<WorkpackModelMenuDto> getWorkpackModelMenuDto(Set<WorkpackModel> workpackModels) {
        Set<WorkpackModelMenuDto> workpackMenuDtos = new HashSet<>(0);
        workpackModels.forEach(model -> {
            WorkpackModelMenuDto menuDto = modelMapper.map(model, WorkpackModelMenuDto.class);
            workpackMenuDtos.add(menuDto);
            if (model.getChildren() != null) {
                menuDto.setChildren(getWorkpackModelMenuDto(model.getChildren()));
            }
        });

        return workpackMenuDtos;
    }

    private Set<WorkpackMenuDto> getWorkpackMenuDto(Set<Workpack> workpacks) {
        Set<WorkpackMenuDto> workpackMenuDtos = new HashSet<>(0);
        workpacks.forEach(workpack -> {
            WorkpackMenuDto menuDto = modelMapper.map(workpack, WorkpackMenuDto.class);
            WorkpackDetailDto detailDto = workpackService.getWorkpackDetailDto(workpack);
            menuDto.setFontIcon(detailDto.getModel().getFontIcon());
            if (detailDto.getModel() != null && detailDto.getModel().getProperties() != null) {
                PropertyModelDto propertyModelName = detailDto.getModel().getProperties().stream().filter(
                    pm -> pm.getName().equals("name")).findFirst().orElse(null);

                PropertyDto propertyName = detailDto.getProperties().stream().filter(
                    pn -> propertyModelName != null && pn.getIdPropertyModel().equals(
                        propertyModelName.getId())).findFirst().orElse(null);

                Property property = workpack.getProperties().stream().filter(
                    p -> propertyName != null && p.getId().equals(propertyName.getId())).findFirst().orElse(null);
                if (property != null) {
                    menuDto.setName((String) workpackService.getValueProperty(property));
                    workpackMenuDtos.add(menuDto);
                }

            }
            if (workpack.getChildren() != null) {
                menuDto.setChildren(getWorkpackMenuDto(workpack.getChildren()));
            }
        });
        return workpackMenuDtos;
    }


}
