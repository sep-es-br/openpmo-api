package br.gov.es.openpmo.scheduler.updatestatus;

import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class UpdateProgramCompletedStatus {

  private final WorkpackRepository workpackRepository;

  @Autowired
  public UpdateProgramCompletedStatus(final WorkpackRepository workpackRepository) {
    this.workpackRepository = workpackRepository;
  }

  @Transactional
  public void update() {
    final Collection<Program> programs = this.findAllPrograms();
    final Collection<Program> projectsCompleted = new ArrayList<>();
    for(final Program project : programs) {
      this.addProjectIfWasCompleted(projectsCompleted, project);
    }
    this.onlySaveNodes(projectsCompleted);
  }

  private Collection<Program> findAllPrograms() {
    return this.workpackRepository.findAllPrograms();
  }

  private void addProjectIfWasCompleted(
    final Collection<? super Program> programsCompleted,
    final Program program
  ) {
    final boolean hasRemainProjectsToComplete = this.hasRemainProjectsToComplete(program);
    if(hasRemainProjectsToComplete) return;
    program.setCompleted(true);
    programsCompleted.add(program);
  }

  private void onlySaveNodes(final Iterable<? extends Workpack> workpacks) {
    this.workpackRepository.save(workpacks, 0);
  }

  private boolean hasRemainProjectsToComplete(final Program program) {
    return this.workpackRepository.hasRemainProjectsToComplete(program.getId());
  }

}
