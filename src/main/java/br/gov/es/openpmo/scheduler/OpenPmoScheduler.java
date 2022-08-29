package br.gov.es.openpmo.scheduler;

import br.gov.es.openpmo.scheduler.cache.CacheCleaner;
import br.gov.es.openpmo.scheduler.updateroles.UpdateLocalRolesUsingRemoteRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OpenPmoScheduler {

  private final UpdateLocalRolesUsingRemoteRoles updateRoles;
  private final CacheCleaner cacheCleaner;

  @Autowired
  public OpenPmoScheduler(
    final UpdateLocalRolesUsingRemoteRoles updateRoles,
    final CacheCleaner cacheCleaner
  ) {
    this.updateRoles = updateRoles;
    this.cacheCleaner = cacheCleaner;
  }

  @Scheduled(cron = "${app.scheduler.everyday-at-0pm}")
  public void updatePersonRoles() {
    this.updateRoles.updatePersonRoles();
  }

  @Scheduled(cron = "${app.scheduler.everyday-at-0pm}")
  public void clearAllCaches() {
    this.cacheCleaner.clearAllCache();
  }

}
