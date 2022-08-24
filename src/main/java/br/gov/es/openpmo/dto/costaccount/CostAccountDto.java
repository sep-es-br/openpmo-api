package br.gov.es.openpmo.dto.costaccount;

import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.CostAccount;

import java.util.*;
import java.util.stream.Collectors;

public class CostAccountDto {
    private Long id;
    private List<? extends PropertyDto> properties;
    private List<PropertyModelDto> models;
    private Long idWorkpack;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public List<? extends PropertyDto> getProperties() {
        return this.properties;
    }

    public void setProperties(final List<? extends PropertyDto> properties) {
        this.properties = properties;
    }

    public List<PropertyModelDto> getModels() {
        return this.models;
    }

    public void setModels(final List<PropertyModelDto> models) {
        this.models = models;
    }

    public Long getIdWorkpack() {
        return this.idWorkpack;
    }

    public void setIdWorkpack(final Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public static CostAccountDto of(final CostAccount costAccount) {
        final CostAccountDto instance = new CostAccountDto();
        instance.setId(costAccount.getId());
        instance.setIdWorkpack(costAccount.getWorkpackId());
        instance.setProperties(getPropertiesFrom(costAccount));
        instance.setModels(getModelsFrom(costAccount));
        return instance;
    }

    private static List<PropertyModelDto> getModelsFrom(final CostAccount costAccount) {
        return Optional.ofNullable(costAccount)
                .map(ca -> ca.getPropertyModels()
                        .stream()
                        .map(PropertyModelDto::of)
                        .collect(Collectors.toList())
                )
                .orElse(new ArrayList<>());
    }

    private static List<PropertyDto> getPropertiesFrom(final CostAccount costAccount) {
        return Optional.of(costAccount.getProperties())
                .map(ca -> ca.stream().map(PropertyDto::of).collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
}
