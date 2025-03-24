package br.gov.es.openpmo.scheduler;

import br.gov.es.openpmo.scheduler.cache.CacheCleaner;
import br.gov.es.openpmo.scheduler.updateActualValues.UpdatePlannedValuesFromActualValues;
import br.gov.es.openpmo.scheduler.updateroles.UpdateLocalRolesUsingRemoteRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OpenPmoScheduler {

  private final UpdateLocalRolesUsingRemoteRoles updateRoles;
  private final CacheCleaner cacheCleaner;
  private final UpdatePlannedValuesFromActualValues updateActualValues;

  @Autowired
  public OpenPmoScheduler(
    final UpdateLocalRolesUsingRemoteRoles updateRoles,
    final CacheCleaner cacheCleaner,
    final UpdatePlannedValuesFromActualValues updateActualValues
  ) {
    this.updateRoles = updateRoles;
    this.cacheCleaner = cacheCleaner;
    this.updateActualValues = updateActualValues;
  }

  @Scheduled(cron = "${app.scheduler.everyday-at-0pm}")
  public void updatePersonRoles() {
    this.updateRoles.updatePersonRoles();
  }

  @Scheduled(cron = "${app.scheduler.everyday-at-0pm}")
  public void clearAllCaches() {
    this.cacheCleaner.clearAllCache();
  }

//  @Scheduled(cron = "${app.scheduler.everyday-every-1mounth}")
//  public void updateActualValues() {
//    this.updateActualValues.updateValuesInSchedule();
//  }

}
