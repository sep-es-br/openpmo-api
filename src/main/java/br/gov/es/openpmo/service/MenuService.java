package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.menu.MenuOfficeDto;
import br.gov.es.openpmo.dto.menu.PlanMenuDto;
import br.gov.es.openpmo.dto.menu.WorkpackMenuDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.PropertyModelDto;
import br.gov.es.openpmo.model.Office;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.Plan;
import br.gov.es.openpmo.model.Property;
import br.gov.es.openpmo.model.Workpack;

@Service
public class MenuService {

    private final WorkpackService workpackService;
    private final PlanService planService;
    private final OfficeService officeService;
    private final ModelMapper modelMapper;
    private final PersonService personService;

    @Autowired
    public MenuService(WorkpackService workpackService, PlanService planService, PersonService personService,
                       OfficeService officeService, ModelMapper modelMapper) {
        this.personService = personService;
        this.workpackService = workpackService;
        this.planService = planService;
        this.officeService = officeService;
        this.modelMapper = modelMapper;
    }

    public List<WorkpackMenuDto> findAllPortfolio(Long idOffice, Long idUser) {
        Person person = personService.findById(idUser);
        List<WorkpackMenuDto> menus = new ArrayList<>(0);
        Office office = officeService.findById(idOffice);
        List<PermissionDto> permissions = workpackService.getOfficePermissionDto(office, person);
        if (person.isAdministrator() || (permissions != null && !permissions.isEmpty())) {
            List<Plan> plans = planService.findAllInOffice(office.getId());
            plans.forEach(plan -> {
                Set<Workpack> workpacks = workpackService.findAllByPlanWithProperties(plan.getId());
                menus.addAll(getWorkpackMenuDto(workpacks));
            });
        }
        return menus;
    }

    public List<MenuOfficeDto> findAllOffice(Long idUser) {
        Person person = personService.findById(idUser);
        List<MenuOfficeDto> menus = new ArrayList<>(0);
        List<Office> offices = officeService.findAll();
        offices.forEach(office -> {
            List<PermissionDto> permissions = workpackService.getOfficePermissionDto(office, person);
            if (person.isAdministrator() || (permissions != null && !permissions.isEmpty())) {
                MenuOfficeDto item = modelMapper.map(office, MenuOfficeDto.class);
                List<Plan> plans = planService.findAllInOffice(office.getId());
                plans.forEach(plan -> {
                    item.getPlans().add(modelMapper.map(plan, PlanMenuDto.class));
                });
                menus.add(item);
            }
        });
        return menus;
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
