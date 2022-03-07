package br.gov.es.openpmo.scheduler.updatestatus;

import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class UpdateProjectsCompletedStatus {

    private final WorkpackRepository workpackRepository;

    @Autowired
    public UpdateProjectsCompletedStatus(final WorkpackRepository workpackRepository) {
        this.workpackRepository = workpackRepository;
    }

    @Transactional
    public void update() {
        final Collection<Project> projects = this.findAllWorkpackProject();
        final Collection<Project> projectsCompleted = new ArrayList<>();
        for (final Project project : projects) {
            this.addProjectIfWasCompleted(projectsCompleted, project);
        }
        this.onlySaveNodes(projectsCompleted);
    }

    private Collection<Project> findAllWorkpackProject() {
        return this.workpackRepository.findAllProjects();
    }

    private void addProjectIfWasCompleted(
            final Collection<? super Project> projectsCompleted,
            final Project project
    ) {
        final boolean hasRemainDeliverablesToComplete = this.hasAllDeliverablesComplete(project);
        if (hasRemainDeliverablesToComplete) return;
        project.setCompleted(true);
        projectsCompleted.add(project);
    }

    private void onlySaveNodes(final Iterable<? extends Workpack> workpacks) {
        this.workpackRepository.save(workpacks, 0);
    }

    private boolean hasAllDeliverablesComplete(final Project project) {
        return this.workpackRepository.hasDeliverableToComplete(project.getId());
    }

}
