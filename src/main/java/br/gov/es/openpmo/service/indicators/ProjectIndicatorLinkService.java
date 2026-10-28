package br.gov.es.openpmo.service.indicators;

import br.gov.es.openpmo.dto.indicators.ProjectIndicatorLinkDto;
import br.gov.es.openpmo.dto.indicators.ProjectIndicatorLinkRequest;
import br.gov.es.openpmo.enumerator.indicators.IndicatorLinkCategory;
import br.gov.es.openpmo.model.indicators.ProjectIndicatorLink;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.ProjectIndicatorLinkRepository;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectIndicatorLinkService {

    private final ProjectIndicatorLinkRepository repository;
    private final WorkpackService workpackService;
    private final ObjectMapper objectMapper;

    public ProjectIndicatorLinkService(
        final ProjectIndicatorLinkRepository repository,
        final WorkpackService workpackService,
        final ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.workpackService = workpackService;
        this.objectMapper = objectMapper;
    }

    public List<ProjectIndicatorLinkDto> findAll(final Long workpackId, final IndicatorLinkCategory category) {
        return repository.findAllByWorkpack(workpackId, category).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public ProjectIndicatorLinkDto create(final Long workpackId, final ProjectIndicatorLinkRequest request) {
        if (repository.findByExternalIdentity(workpackId, request.getCategory(), request.getExternalId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este item já está vinculado ao projeto.");
        }
        final Workpack workpack = workpackService.findByIdDefault(workpackId);
        final ProjectIndicatorLink link = new ProjectIndicatorLink(
            request.getCategory(),
            request.getExternalId(),
            request.getName().trim(),
            request.getDescription(),
            workpack
        );
        link.setProjectGoalsJson(writeProjectGoals(request.getProjectGoals()));
        return toDto(repository.save(link));
    }

    @Transactional
    public ProjectIndicatorLinkDto updateActive(
        final Long workpackId,
        final Long linkId,
        final boolean active
    ) {
        final ProjectIndicatorLink link = repository.findByIdAndWorkpack(linkId, workpackId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vínculo não encontrado."));
        link.setActive(active);
        return toDto(repository.save(link));
    }

    @Transactional
    public ProjectIndicatorLinkDto updateProjectGoals(
        final Long workpackId,
        final Long linkId,
        final Map<String, BigDecimal> projectGoals
    ) {
        final ProjectIndicatorLink link = repository.findByIdAndWorkpack(linkId, workpackId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vínculo não encontrado."));
        link.setProjectGoalsJson(writeProjectGoals(projectGoals));
        return toDto(repository.save(link));
    }

    private ProjectIndicatorLinkDto toDto(final ProjectIndicatorLink link) {
        return ProjectIndicatorLinkDto.of(link, readProjectGoals(link.getProjectGoalsJson()));
    }

    private String writeProjectGoals(final Map<String, BigDecimal> projectGoals) {
        if (projectGoals == null || projectGoals.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(projectGoals);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Metas do projeto inválidas.", exception);
        }
    }

    private Map<String, BigDecimal> readProjectGoals(final String projectGoalsJson) {
        if (projectGoalsJson == null || projectGoalsJson.trim().isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(projectGoalsJson, new TypeReference<Map<String, BigDecimal>>() {});
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Metas do projeto armazenadas em formato inválido.", exception);
        }
    }
}
