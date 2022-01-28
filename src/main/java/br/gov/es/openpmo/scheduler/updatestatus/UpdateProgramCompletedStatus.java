package br.gov.es.openpmo.scheduler.updatestatus;

import br.gov.es.openpmo.model.workpacks.Program;
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
public class UpdateProgramCompletedStatus {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProgramCompletedStatus.class);
  private final WorkpackRepository workpackRepository;

  @Autowired
  public UpdateProgramCompletedStatus(final WorkpackRepository workpackRepository) {
    this.workpackRepository = workpackRepository;
  }

  @Transactional
  public void update() {
    LOGGER.info("Initializing update of programs");
    final Collection<Program> programs = this.findAllPrograms();
    LOGGER.info("Found {} workpacks programs", programs.size());
    final Collection<Program> projectsCompleted = new ArrayList<>();
    for(final Program project : programs) {
      this.addProjectIfWasCompleted(projectsCompleted, project);
    }
    LOGGER.info("Update status to completed of {} programs", projectsCompleted.size());
    this.onlySaveNodes(projectsCompleted);
    LOGGER.info("Finalizing update of programs");
  }

  private void addProjectIfWasCompleted(
    final Collection<? super Program> programsCompleted,
    final Program program
  ) {
    final boolean hasRemainProjectsToComplete = this.hasRemainProjectsToComplete(program);

    LOGGER.info("Program {} was completed: {}", program.getId(), !hasRemainProjectsToComplete);

    if(hasRemainProjectsToComplete) return;

    program.setCompleted(true);
    programsCompleted.add(program);
  }

  private boolean hasRemainProjectsToComplete(final Program program) {
    return this.workpackRepository.hasRemainProjectsToComplete(program.getId());
  }

  private Collection<Program> findAllPrograms() {
    return this.workpackRepository.findAllPrograms();
  }

  private void onlySaveNodes(final Iterable<? extends Workpack> workpacks) {
    this.workpackRepository.save(workpacks, 0);
  }

}
