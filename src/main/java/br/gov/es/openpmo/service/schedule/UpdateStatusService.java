package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.dashboards.v2.IAsyncDashboardService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UpdateStatusService {

    private final WorkpackRepository workpackRepository;
    private final StepRepository stepRepository;
    private final IAsyncDashboardService dashboardService;

    public UpdateStatusService(
            WorkpackRepository workpackRepository,
            StepRepository stepRepository,
            IAsyncDashboardService dashboardService
    ) {
        this.workpackRepository = workpackRepository;
        this.stepRepository = stepRepository;
        this.dashboardService = dashboardService;
    }

    public List<Deliverable> getDeliverablesByStepId(Long stepId) {
        return this.stepRepository.findAllDeliverablesByStepId(stepId);
    }

    public List<Deliverable> getDeliverablesByScheduleId(Long stepId) {
        return this.stepRepository.findDeliverablesByScheduleId(stepId);
    }

    public void update(List<Deliverable> deliverables) {
        final List<Workpack> completedDeliverables = new ArrayList<>();
        for (Deliverable deliverable : deliverables) {
            if (Optional.ofNullable(deliverable.getCompleted()).orElse(false)) {
                updateIfCompleted(deliverable, completedDeliverables);
            }
        }
        this.onlySaveNodes(completedDeliverables);
        this.updateDashboards(deliverables);
    }

    private void updateIfCompleted(Deliverable deliverable, List<Workpack> completedDeliverables) {
        final boolean hasScheduleRelated = this.hasScheduleRelated(deliverable);
        if (!hasScheduleRelated) {
            return;
        }
        final boolean hasWorkToComplete = this.hasWorkToComplete(deliverable.getId());
        if (!hasWorkToComplete) return;
        deliverable.setCompleted(false);
        completedDeliverables.add(deliverable);

        final Optional<Project> project = this.workpackRepository.findProject(deliverable.getId());
        if (project.isPresent()) {
            project.get().setCompleted(false);
            completedDeliverables.add(project.get());
        }

        final Optional<Program> program = this.workpackRepository.findProgram(deliverable.getId());
        if (program.isPresent()) {
            program.get().setCompleted(false);
            completedDeliverables.add(program.get());
        }
    }

    private void onlySaveNodes(final Iterable<? extends Workpack> workpacks) {
        this.workpackRepository.save(workpacks, 0);
    }

    private boolean hasScheduleRelated(final Deliverable deliverable) {
        return this.workpackRepository.hasScheduleRelated(deliverable.getId());
    }

    private boolean hasWorkToComplete(final Long idDeliverable) {
        return this.hasBaselineActive(idDeliverable)
                ? this.hasWorkToCompleteComparingWithActiveBaseline(idDeliverable)
                : this.hasWorkToCompleteComparingWithMaster(idDeliverable);
    }

    private boolean hasBaselineActive(final Long idDeliverable) {
        return this.workpackRepository.hasActiveBaseline(idDeliverable);
    }

    private boolean hasWorkToCompleteComparingWithActiveBaseline(final Long idDeliverable) {
        return this.stepRepository.hasWorkToCompleteComparingWithActiveBaseline(idDeliverable);
    }

    private boolean hasWorkToCompleteComparingWithMaster(final Long idDeliverable) {
        return this.stepRepository.hasWorkToCompleteComparingWithMaster(idDeliverable);
    }

    public void updateDashboards(List<Deliverable> deliverables) {
        final List<Long> deliverablesId = deliverables.stream()
                .map(Deliverable::getId)
                .collect(Collectors.toList());

        this.stepRepository.findAllDeliverablesAndAscendents(deliverablesId)
                .stream()
                .map(Workpack::getId)
                .forEach(this.dashboardService::calculate);
    }

}
