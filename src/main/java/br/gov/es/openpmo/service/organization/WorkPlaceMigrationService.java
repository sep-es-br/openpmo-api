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
    final Long migrated = this.repository.migrateLegacyContacts();
    if(migrated != null && migrated > 0L) {
      LOGGER.info("Migrated {} contact-book relationships to WorkPlace nodes with legacy backups.", migrated);
    }
    final Long migratedOrganizations = this.repository.migrateLegacyOrganizations();
    if(migratedOrganizations != null && migratedOrganizations > 0L) {
      LOGGER.info("Migrated {} person-organization relationships to WorkPlace nodes with legacy backups.", migratedOrganizations);
    }
  }
}
