package br.gov.es.openpmo.service.organization;

import br.gov.es.openpmo.repository.WorkPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WorkPlaceMigrationService implements ApplicationRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkPlaceMigrationService.class);

  private final WorkPlaceRepository repository;

  public WorkPlaceMigrationService(final WorkPlaceRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional
  public void run(final ApplicationArguments args) {
    log("Repaired contact relationships that incorrectly targeted WorkPlace nodes",
      this.repository.repairIncorrectContactTargets());
    log("Restored direct contact relationships and removed accidental legacy relationships",
      this.repository.removeAccidentalLegacyContacts());
    log("Repaired Organization-IS-WorkPlace relationship directions",
      this.repository.repairOrganizationDirection());
    log("Removed WorkPlace nodes without OF-Person or FOR-Office",
      this.repository.removeInvalidWorkPlaces());
    log("Created missing WorkPlace nodes", this.repository.createMissingWorkPlaces());
    log("Migrated Person-WORKS_IN-Organization relationships",
      this.repository.migratePersonOrganizations());
    log("Removed accidental legacy organization relationships",
      this.repository.removeAccidentalLegacyOrganizations());
  }

  private static void log(final String operation, final Long affected) {
    if(affected != null && affected > 0L) {
      LOGGER.info("{}: {}.", operation, affected);
    }
  }
}
