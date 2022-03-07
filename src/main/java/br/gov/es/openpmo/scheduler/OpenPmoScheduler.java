package br.gov.es.openpmo.scheduler;

import br.gov.es.openpmo.scheduler.updateroles.UpdateLocalRolesUsingRemoteRoles;
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

    @Scheduled(cron = "${app.scheduler.everyday-at-0pm}")
    public void updatePersonRoles() {
        this.updateRoles.updatePersonRoles();
    }

}
