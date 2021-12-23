package br.gov.es.openpmo.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OpenPmoScheduler {

  private final UpdateLocalRolesUsingRemoteRoles updateRoles;

  @Autowired
  public OpenPmoScheduler(final UpdateLocalRolesUsingRemoteRoles updateRoles) {
    this.updateRoles = updateRoles;
  }

  @Scheduled(cron = "0 0 12 * * ?")
  public void updatePersonRoles() {
    this.updateRoles.updatePersonRoles();
  }

}
