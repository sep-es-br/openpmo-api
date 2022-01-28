package br.gov.es.openpmo.scheduler.updatestatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateWorkpackCompletedStatus {

  private final UpdateDeliverablesCompletedStatus updateDeliverablesCompletedStatus;
  private final UpdateProjectsCompletedStatus updateProjectsCompletedStatus;
  private final UpdateProgramCompletedStatus updateProgramCompletedStatus;

  @Autowired
  public UpdateWorkpackCompletedStatus(
    final UpdateDeliverablesCompletedStatus updateDeliverablesCompletedStatus,
    final UpdateProjectsCompletedStatus updateProjectsCompletedStatus,
    final UpdateProgramCompletedStatus updateProgramCompletedStatus
  ) {
    this.updateDeliverablesCompletedStatus = updateDeliverablesCompletedStatus;
    this.updateProjectsCompletedStatus = updateProjectsCompletedStatus;
    this.updateProgramCompletedStatus = updateProgramCompletedStatus;
  }

  public void update() {
    this.updateDeliverablesCompletedStatus.update();
    this.updateProjectsCompletedStatus.update();
    this.updateProgramCompletedStatus.update();
  }
}
