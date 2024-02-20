package br.gov.es.openpmo.service.permissions.canaccess;

import br.gov.es.openpmo.repository.permissions.PermissionRepository;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization.PersonDataResponse;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class CanAccessData implements ICanAccessData {

  private final IGetPersonFromAuthorization getPersonFromAuthorization;
  private final PermissionRepository permissionRepository;

  private final Logger logger;

  public CanAccessData(
    final IGetPersonFromAuthorization getPersonFromAuthorization,
    final PermissionRepository permissionRepository,
    final Logger logger
  ) {
    this.getPersonFromAuthorization = getPersonFromAuthorization;
    this.permissionRepository = permissionRepository;
    this.logger = logger;
  }

  private static boolean isAdministrator(final PersonDataResponse personData) {
    return personData.getPerson().getAdministrator();
  }

  private static boolean hasPermission(
    final List<Long> ids,
    final String key,
    final BiPredicate<? super List<Long>, ? super String> permissionFunction
  ) {
    return permissionFunction.test(ids, key);
  }

  private static Boolean isSelfId(
    final PersonDataResponse personData,
    final Collection<Long> ids
  ) {
    return ids.stream()
      .filter(Objects::nonNull)
      .anyMatch(argId -> argId.equals(getId(personData)));
  }

  private static Long getId(final PersonDataResponse personData) {
    return personData.getPerson().getId();
  }

  @Override
  public CanAccessDataResponse execute(
    final Long id,
    final String authorizationHeader
  ) {
    final PersonDataResponse personData = this.getPerson(authorizationHeader);
    final boolean isAdministrator = isAdministrator(personData);

    final List<Long> ids = Collections.singletonList(id);
    final Boolean self = isSelfId(personData, ids);

    if (isAdministrator) {
      return CanAccessDataResponse.administrator(personData.getKey(), self);
    }
    Instant t1 = Instant.now();
    final boolean editManagement = hasPermission(
      ids,
      personData.getKey(),
      this.permissionRepository::hasEditManagementPermission
    );
    this.logger.error("Tempo execução hasEditManagementPermission -> " + ChronoUnit.MILLIS.between(t1, Instant.now()));

    Instant t2 = Instant.now();
    final boolean edit = hasPermission(ids, personData.getKey(), this.permissionRepository::hasEditPermission);
    this.logger.error("Tempo execução hasEditPermission -> " + ChronoUnit.MILLIS.between(t2, Instant.now()));
    if (edit) {
      return CanAccessDataResponse.edit(personData.getKey(), editManagement, self);
    }
    Instant t3 = Instant.now();
    final boolean read = hasPermission(ids, personData.getKey(), this.permissionRepository::hasReadPermission);
    this.logger.error("Tempo execução hasReadPermission -> " + ChronoUnit.MILLIS.between(t3, Instant.now()));
    if (read) {
      return CanAccessDataResponse.read(personData.getKey(), editManagement, self);
    }

    Instant t4 = Instant.now();
    final CanAccessDataResponse canAccess = new CanAccessDataResponse(
            false,
            false,
            hasPermission(ids, personData.getKey(), this.permissionRepository::hasBasicReadPermission),
            false,
            self,
            personData.getKey(),
            new CanAccessManagementDataResponse(
                    editManagement,
                    true
            )
    );
    this.logger.error("Tempo execução hasBasicReadPermission -> " + ChronoUnit.MILLIS.between(t4, Instant.now()));
    return canAccess;
  }

  @Override
  public ICanAccessDataResponse execute(
    final List<Long> ids,
    final String authorizationHeader
  ) {
    final PersonDataResponse personData = this.getPerson(authorizationHeader);
    final boolean isAdministrator = isAdministrator(personData);

    final Boolean self = isSelfId(personData, ids);

    if (isAdministrator) {
      return CanAccessDataResponse.administrator(personData.getKey(), self);
    }

    final boolean editManagement = hasPermission(
      ids,
      personData.getKey(),
      this.permissionRepository::hasEditManagementPermission
    );

    final boolean edit = hasPermission(ids, personData.getKey(), this.permissionRepository::hasEditPermission);
    if (edit) {
      return CanAccessDataResponse.edit(personData.getKey(), editManagement, self);
    }
    final boolean read = hasPermission(ids, personData.getKey(), this.permissionRepository::hasReadPermission);
    if (read) {
      return CanAccessDataResponse.read(personData.getKey(), editManagement, self);
    }

    return new CanAccessDataResponse(
      false,
      false,
      hasPermission(ids, personData.getKey(), this.permissionRepository::hasBasicReadPermission),
      false,
      self,
      personData.getKey(),
      new CanAccessManagementDataResponse(
        editManagement,
        true
      )
    );
  }

  @Override
  public ICanAccessDataResponse execute(final String authorizationHeader) {
    final PersonDataResponse personData = this.getPerson(authorizationHeader);
    return new CanAccessDataResponse(
      null,
      null,
      null,
      isAdministrator(personData),
      false,
      personData.getKey(),
      null
    );
  }

  private PersonDataResponse getPerson(final String authorizationHeader) {
    return this.getPersonFromAuthorization.execute(authorizationHeader);
  }

}
