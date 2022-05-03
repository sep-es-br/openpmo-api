package br.gov.es.openpmo.service.filters;

import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.CustomFilterEnum;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.service.workpack.WorkpackModelService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CustomFilterService {

    private final CustomFilterRepository customFilterRepository;
    private final RulesService rulesService;
    private final WorkpackModelService workpackModelService;

    @Autowired
    public CustomFilterService(
            final CustomFilterRepository customFilterRepository,
            final RulesService rulesService,
            final WorkpackModelService workpackModelService
    ) {
        this.customFilterRepository = customFilterRepository;
        this.rulesService = rulesService;
        this.workpackModelService = workpackModelService;
    }

    @Transactional
    public CustomFilterDto create(@Valid final CustomFilterDto request, final CustomFilterEnum customFilterEnum, final Long idWorkPackModel) {
        WorkpackModel workpackModel = null;
        if (idWorkPackModel != null) {
            workpackModel = this.workpackModelService.findById(idWorkPackModel);
        }

        final CustomFilter entity = new CustomFilter(
                request.getName(),
                customFilterEnum,
                request.getFavorite(),
                request.getSortByDirection(),
                request.getSortBy(),
                workpackModel
        );
        this.customFilterRepository.save(entity);
        final List<Rules> rules = this.createRules(entity, request);

        return this.buildCustomFilterDto(entity, new HashSet<>(rules));
    }

    private List<Rules> createRules(final CustomFilter entity, final CustomFilterDto request) {
        return this.rulesService.createAll(entity, request.getRules());
    }

    private CustomFilterDto buildCustomFilterDto(final CustomFilter customFilter, final Set<Rules> rules) {
        final CustomFilterDto customFilterDto = new CustomFilterDto();
        customFilterDto.setFavorite(customFilter.isFavorite());
        customFilterDto.setId(customFilter.getId());
        customFilterDto.setName(customFilter.getName());
        customFilterDto.setSortBy(customFilter.getSortBy());
        customFilterDto.setSortByDirection(customFilter.getDirection());

        customFilterDto.setRules(this.buildCustomFilterRulesDto(rules));
        return customFilterDto;
    }

    private List<CustomFilterRulesDto> buildCustomFilterRulesDto(final Set<Rules> rules) {
        final List<CustomFilterRulesDto> listCustomFilterRulesDto = new ArrayList<>();

        if (rules == null) {
            return listCustomFilterRulesDto;
        }

        for (final Rules rule : rules) {
            final CustomFilterRulesDto dto = new CustomFilterRulesDto();
            dto.setPropertyName(rule.getPropertyName());
            dto.setOperator(rule.getRelationalOperator());
            dto.setValue(rule.getValue());
            dto.setLogicOperator(rule.getLogicOperator());
            dto.setId(rule.getId());
            listCustomFilterRulesDto.add(dto);
        }

        return listCustomFilterRulesDto;
    }

    public CustomFilterDto update(
            @Valid final CustomFilterDto request,
            final CustomFilterEnum customFilterEnum,
            final Long idWorkPackModel
    ) {
        final CustomFilter customFilterToUpdate = this.findById(request.getId());

        WorkpackModel workpackModel = null;
        if (idWorkPackModel != null) {
            workpackModel = this.workpackModelService.findById(idWorkPackModel);
        }

        customFilterToUpdate.update(
                request,
                customFilterEnum,
                customFilterToUpdate,
                workpackModel
        );

        this.customFilterRepository.save(customFilterToUpdate);

        this.updateRulesRelationship(customFilterToUpdate, request.getRules());

        return this.buildCustomFilterDto(customFilterToUpdate, customFilterToUpdate.getRules());
    }

    private void updateRulesRelationship(final CustomFilter entity, final List<CustomFilterRulesDto> rules) {
        this.rulesService.update(entity, rules);
    }

    public CustomFilter findById(final Long id) {
        return this.customFilterRepository.findById(id)
                .orElseThrow(() -> new NegocioException(ApplicationMessage.CUSTOM_FILTER_NOT_FOUND));
    }

    public List<CustomFilterDto> getCombo(final CustomFilterEnum customFilterEnum) {
        final List<CustomFilter> customFilters = this.customFilterRepository.findByType(customFilterEnum);

        List<CustomFilterDto> result = new ArrayList<>();

        for (CustomFilter filter : customFilters) {
            final Set<Rules> rules = filter.getRules();
            final CustomFilterDto filterDto = this.buildCustomFilterDto(filter, rules);
            result.add(filterDto);
        }

        return result;
    }

    public CustomFilterDto getById(final Long id) {
        final CustomFilter customFilter = this.findById(id);
        final Set<Rules> rules = customFilter.getRules();

        return this.buildCustomFilterDto(customFilter, rules);
    }

    public void delete(final Long id) {
        final CustomFilter customFilter = this.findById(id);
        this.rulesService.deleteByCustomFilter(customFilter);
        this.customFilterRepository.delete(customFilter);
    }

    public List<?> getCombo(Long workpackModelId, CustomFilterEnum customFilterEnum) {
        final List<CustomFilter> customFilters =
                this.customFilterRepository.findByWorkpackModelIdAndType(workpackModelId, customFilterEnum);

        List<CustomFilterDto> result = new ArrayList<>();

        for (CustomFilter filter : customFilters) {
            final Set<Rules> rules = filter.getRules();
            final CustomFilterDto filterDto = this.buildCustomFilterDto(filter, rules);
            result.add(filterDto);
        }

        return result;
    }
}
