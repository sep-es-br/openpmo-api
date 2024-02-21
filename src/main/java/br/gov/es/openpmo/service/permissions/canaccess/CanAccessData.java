package br.gov.es.openpmo.service.permissions.canaccess;

import br.gov.es.openpmo.repository.permissions.PermissionRepository;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization.PersonDataResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

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
    final boolean editManagement = hasPermission(
      ids,
      personData.getKey(),
      this.permissionRepository::hasEditManagementPermission
    );
    if (editManagement) {
      return CanAccessDataResponse.edit(personData.getKey(), editManagement, self);
    }

    final boolean edit = hasPermission(ids, personData.getKey(), this.permissionRepository::hasEditPermission);
    if (edit) {
      return CanAccessDataResponse.edit(personData.getKey(), editManagement, self);
    }

    final boolean read = hasPermission(ids, personData.getKey(), this.permissionRepository::hasReadPermission);
    if (read) {
      return CanAccessDataResponse.read(personData.getKey(), editManagement, self);
    }

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
