package br.gov.es.openpmo.service.permissions.canaccess;

import br.gov.es.openpmo.repository.permissions.PermissionRepository;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization.PersonDataResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

@Component
public class CanAccessData implements ICanAccessData {

  private final IGetPersonFromAuthorization getPersonFromAuthorization;
  private final PermissionRepository permissionRepository;

  public CanAccessData(
    final IGetPersonFromAuthorization getPersonFromAuthorization,
    final PermissionRepository permissionRepository
  ) {
    this.getPersonFromAuthorization = getPersonFromAuthorization;
    this.permissionRepository = permissionRepository;
  }

  private static boolean isAdministrator(final PersonDataResponse personData) {
    return personData.getPerson().getAdministrator();
  }

  private static boolean hasPermission(
    final List<Long> ids,
    final String key,
    final BiFunction<? super List<Long>, ? super String, Long> permissionFunction
  ) {
    final Long permissions = permissionFunction.apply(ids, key);
    return permissions > 0;
  }

  private static Boolean isSelfId(
    final PersonDataResponse personData,
    final List<Long> ids
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

    final List<Long> ids = Collections.singletonList(id);
    return new CanAccessDataResponse(
      hasPermission(ids, personData.getKey(), this.permissionRepository::hasEditPermission),
      hasPermission(ids, personData.getKey(), this.permissionRepository::hasReadPermission),
      hasPermission(ids, personData.getKey(), this.permissionRepository::hasBasicReadPermission),
      isAdministrator(personData),
      isSelfId(personData, ids),
      personData.getKey(),
      new CanAccessManagementDataResponse(
        hasPermission(ids, personData.getKey(), this.permissionRepository::hasEditManagementPermission),
        true
      )
    );
  }

  @Override
  public ICanAccessDataResponse execute(
    final List<Long> ids,
    final String authorizationHeader
  ) {
    final PersonDataResponse personData = this.getPerson(authorizationHeader);
    return new CanAccessDataResponse(
      hasPermission(ids, personData.getKey(), this.permissionRepository::hasEditPermission),
      hasPermission(ids, personData.getKey(), this.permissionRepository::hasReadPermission),
      hasPermission(ids, personData.getKey(), this.permissionRepository::hasBasicReadPermission),
      isAdministrator(personData),
      isSelfId(personData, ids),
      personData.getKey(),
      new CanAccessManagementDataResponse(
        hasPermission(ids, personData.getKey(), this.permissionRepository::hasEditManagementPermission),
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
