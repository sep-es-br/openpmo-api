package br.gov.es.openpmo.service.indicators;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.indicators.IndicatorCardDto;
import br.gov.es.openpmo.dto.indicators.IndicatorCreateDto;
import br.gov.es.openpmo.dto.indicators.IndicatorDetailDto;
import br.gov.es.openpmo.dto.indicators.IndicatorUpdateDto;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.indicators.Indicator;
import br.gov.es.openpmo.model.indicators.PeriodGoal;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.IndicatorRepository;
import br.gov.es.openpmo.repository.PeriodGoalRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllIndicatorsUsingCustomFilter;
import br.gov.es.openpmo.service.filters.CustomFilterService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.ID_WORKPACK_NOT_NULL;
import static br.gov.es.openpmo.utils.ApplicationMessage.INDICATOR_NOT_FOUND;

@Service
public class IndicatorService {

    private final IndicatorRepository repository;

    private final WorkpackService workpackService;

    private final CustomFilterService customFilterService;

    private final AppProperties appProperties;

    private final FindAllIndicatorsUsingCustomFilter findAllIndicators;

    private final PeriodGoalRepository periodGoalRepository;

    public IndicatorService(
        final IndicatorRepository repository,
        final WorkpackService workpackService,
        final CustomFilterService customFilterService,
        final AppProperties appProperties,
        final FindAllIndicatorsUsingCustomFilter findAllIndicators,
        final PeriodGoalRepository periodGoalRepository
    ) {
        this.repository = repository;
        this.workpackService = workpackService;
        this.customFilterService = customFilterService;
        this.appProperties = appProperties;
        this.findAllIndicators = findAllIndicators;
        this.periodGoalRepository = periodGoalRepository;
    }

    public Indicator create(
            final IndicatorCreateDto request
    ) {
        if (request.getIdWorkpack() == null) {
            throw new IllegalArgumentException(ID_WORKPACK_NOT_NULL);
        }
        final Workpack workpack = this.workpackService.findByIdDefault(request.getIdWorkpack());
        final Indicator indicator = Indicator.of(
                request,
                workpack
        );
        this.repository.save(indicator);
        return indicator;
    }

    public IndicatorDetailDto update(
            final @Valid IndicatorUpdateDto request,
            final Long idPerson
    ) {
        final Indicator indicator = this.findById(request.getId());

        indicator.update(request);

        this.repository.save(
                indicator,
                0
        );
        return IndicatorDetailDto.of(indicator);
    }

    public List<IndicatorCardDto> findAllAsCardDto(
            final Long idWorkpack,
            final Long idFilter,
            final String term,
            final Long idPerson
    ) {
        if (idWorkpack == null) {
            throw new IllegalArgumentException(ID_WORKPACK_NOT_NULL);
        }

        if (idFilter == null) {
            return this.findAllAsCardDto(
                    idWorkpack,
                    term
            );
        }

        final CustomFilter customFilter = this.customFilterService.findById(
                idFilter,
                idPerson
        );

        final Map<String, Object> params = new HashMap<>();
        params.put(
                "idWorkpack",
                idWorkpack
        );
        params.put(
                "term",
                term
        );
        params.put(
                "searchCutOffScore",
                appProperties.getSearchCutOffScore()
        );

        final List<Indicator> indicators = this.findAllIndicators.execute(
                customFilter,
                params
        );
        return indicators.stream()
                .map(IndicatorCardDto::of)
                .collect(Collectors.toList());

    }

    private List<IndicatorCardDto> findAllAsCardDto(
            final Long idWorkpack,
            String term
    ) {
        return this.repository.findAll(
                        idWorkpack,
                        term,
                        appProperties.getSearchCutOffScore()
                ).stream()
                .map(IndicatorCardDto::of)
                .collect(Collectors.toList());
    }

    public IndicatorDetailDto findByIdAsIndicatorDetail(final Long id) {
        Indicator indicator = this.findById(id);

        List<PeriodGoal> achievedGoals = this.repository.findAchievedGoalsByIndicatorId(id)
                .orElse(Collections.emptyList());
        List<PeriodGoal> expectedGoals = this.repository.findExpectedGoalsByIndicatorId(id)
                .orElse(Collections.emptyList());

        IndicatorDetailDto dto = IndicatorDetailDto.of(indicator);
        dto.setAchievedGoals(achievedGoals);
        dto.setExpectedGoals(expectedGoals);

        return dto;
    }

    public Indicator findById(final Long id) {
        return this.repository.findIndicatorDetailById(id)
                .orElseThrow(() -> new IllegalArgumentException(INDICATOR_NOT_FOUND));
    }

    @Transactional
    public void deleteById(final Long id) {

        Indicator indicator = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(INDICATOR_NOT_FOUND));

        indicator.getExpectedGoals().forEach(periodGoalRepository::delete);
        indicator.getAchievedGoals().forEach(periodGoalRepository::delete);

        this.repository.delete(indicator);
    }

    public List<Integer> findUniqueYearsByProjectId(final Long idWorkpack) {
        return this.repository.findUniqueYearsByProjectId(idWorkpack);
    }

    public List<String> findAllOrganizationFromOffice(final Long idOffice) {
        return this.repository.findAllOrganizationFromOffice(idOffice);
    }
}
