package br.gov.es.openpmo.scheduler;

import br.gov.es.openpmo.scheduler.updateroles.UpdateLocalRolesUsingRemoteRoles;
import br.gov.es.openpmo.scheduler.updatestatus.UpdateWorkpackCompletedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OpenPmoScheduler {

  private final UpdateLocalRolesUsingRemoteRoles updateRoles;
  private final UpdateWorkpackCompletedStatus updateWorkpackCompletedStatus;

  @Autowired
  public OpenPmoScheduler(
    final UpdateLocalRolesUsingRemoteRoles updateRoles,
    final UpdateWorkpackCompletedStatus updateWorkpackCompletedStatus
  ) {
    this.updateRoles = updateRoles;
    this.updateWorkpackCompletedStatus = updateWorkpackCompletedStatus;
  }

  @Scheduled(cron = "${app.scheduler.everyday-at-0pm}")
  public void updatePersonRoles() {
    this.updateRoles.updatePersonRoles();
  }

  @Scheduled(cron = "${app.scheduler.everyday-at-0pm}")
  public void updateWorkpackCompletedStatus() {
    this.updateWorkpackCompletedStatus.update();
  }
}
