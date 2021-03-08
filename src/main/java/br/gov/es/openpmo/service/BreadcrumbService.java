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
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDto;
import br.gov.es.openpmo.model.Locality;
import br.gov.es.openpmo.model.Property;
import br.gov.es.openpmo.model.Workpack;
import br.gov.es.openpmo.model.WorkpackModel;

@Service
public class BreadcrumbService {

    private final WorkpackService workpackService;
    private final WorkpackModelService workpackModelService;
    private final LocalityService localityService;

    @Autowired
    public BreadcrumbService(WorkpackService workpackService, WorkpackModelService workpackModelService,
                             LocalityService localityService) {
        this.workpackService = workpackService;
        this.workpackModelService = workpackModelService;
        this.localityService = localityService;
    }

    public List<BreadcrumbDto> localities(Long id) {
        List<BreadcrumbDto> breadcrumbs = new ArrayList<>(0);
        Locality locality = localityService.findByIdWithParent(id);
        breadcrumbs.add(new BreadcrumbDto(locality.getId(), locality.getName(), locality.getFullName(), locality.getType().name()));
        if (locality.getParent() != null) {
            addParentBreadcrumbDto(locality.getParent(), breadcrumbs);
        }
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
        Collections.reverse(breadcrumbs);
        return breadcrumbs;
    }

    public List<BreadcrumbDto> models(Long id) {
        List<BreadcrumbDto> breadcrumbs = new ArrayList<>(0);
        WorkpackModel workpackModel = workpackModelService.findByIdWithParents(id);
        breadcrumbs.add(new BreadcrumbDto(workpackModel.getId(), workpackModel.getModelName(), workpackModel.getModelNameInPlural(), workpackModel.getClass().getSimpleName()));
        if (workpackModel.getParent() != null) {
            addParentBreadcrumbDto(workpackModel.getParent(), breadcrumbs);
        }
        Collections.reverse(breadcrumbs);
        return breadcrumbs;
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
