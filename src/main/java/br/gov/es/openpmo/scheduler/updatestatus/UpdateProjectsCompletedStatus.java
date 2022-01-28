package br.gov.es.openpmo.scheduler.updatestatus;

import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class UpdateProjectsCompletedStatus {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProjectsCompletedStatus.class);
  private final WorkpackRepository workpackRepository;

  @Autowired
  public UpdateProjectsCompletedStatus(final WorkpackRepository workpackRepository) {
    this.workpackRepository = workpackRepository;
  }

  @Transactional
  public void update() {
    LOGGER.info("Initializing update of projects");
    final Collection<Project> projects = this.findAllWorkpackProject();
    LOGGER.info("Found {} workpacks projects", projects.size());
    final Collection<Project> projectsCompleted = new ArrayList<>();
    for(final Project project : projects) {
      this.addProjectIfWasCompleted(projectsCompleted, project);
    }
    LOGGER.info("Update status to completed of {} projects", projectsCompleted.size());
    this.onlySaveNodes(projectsCompleted);
    LOGGER.info("Finalizing update of projects");
  }

  private void addProjectIfWasCompleted(
    final Collection<? super Project> projectsCompleted,
    final Project project
  ) {
    final boolean hasRemainDeliverablesToComplete = this.hasAllDeliverablesComplete(project);

    LOGGER.info("Project {} was completed: {}", project.getId(), !hasRemainDeliverablesToComplete);

    if(hasRemainDeliverablesToComplete) return;

    project.setCompleted(true);
    projectsCompleted.add(project);
  }

  private boolean hasAllDeliverablesComplete(final Project project) {
    return this.workpackRepository.hasDeliverableToComplete(project.getId());
  }

  private Collection<Project> findAllWorkpackProject() {
    return this.workpackRepository.findAllProjects();
  }

  private void onlySaveNodes(final Iterable<? extends Workpack> workpacks) {
    this.workpackRepository.save(workpacks, 0);
  }

}
