package br.gov.es.openpmo.service.permissions.canaccess;

import br.gov.es.openpmo.repository.permissions.PermissionRepository;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization.PersonDataResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CanAccessData implements ICanAccessData {

  private final IGetPersonFromAuthorization getPersonFromAuthorization;
  private final PermissionRepository permissionRepository;

  private final Logger logger = LoggerFactory.getLogger(CanAccessData.class);
  
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
    final List<Long> ids = Collections.singletonList(id);
    return this.execute(ids, authorizationHeader);
  }

  @Override
  public CanAccessDataResponse executeWorkpack(Long idWorkpack, String authorizationHeader) {
    final PersonDataResponse personData = this.getPerson(authorizationHeader);
    final boolean isAdministrator = isAdministrator(personData);
    if (isAdministrator) {
      return CanAccessDataResponse.administrator(personData.getKey(), false);
    }
    boolean hasPermisionOfficeOrPlan = this.permissionRepository.hasPermisionOfficeOrPlan(idWorkpack, personData.getKey());
    if (hasPermisionOfficeOrPlan) {
      return CanAccessDataResponse.read(personData.getKey(), false, false);
    }

    final boolean hasPermission = this.permissionRepository.hasPermissionWorkpack(idWorkpack, personData.getKey());
    if (hasPermission) {
      return CanAccessDataResponse.read(personData.getKey(), false, false);
    }
    return new CanAccessDataResponse(
        false,
        false,
        false,
        false,
        false,
        false,
        personData.getKey(),
        new CanAccessManagementDataResponse(
            false,
            false
        )
    );
  }


  @Override
  public CanAccessDataResponse execute(
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

    if (editManagement) {
      return CanAccessDataResponse.edit(personData.getKey(), true, self);
    }
    
    long tm = System.currentTimeMillis();
    
    
    // 1. Faz UMA ÚNICA travessia no grafo
      Map<String, Object> accessInfo = this.permissionRepository.fetchAccessInfo(ids, personData.getKey());
     
    List<String> permissions = accessInfo != null && accessInfo.get("permissions") != null 
        ? ((List) accessInfo.get("permissions"))
        : Collections.emptyList();

    List<String> statusList = accessInfo != null && accessInfo.get("statusList") != null 
        ? ((List) accessInfo.get("statusList"))
        : Collections.emptyList();

    // 2. Resolve as regras de negócio puramente em memória (ordem de prioridade)

    // Regra EDIT
    boolean hasEdit = permissions.contains("EDIT") || 
                     (permissions.contains("UPDATE") && statusList.contains("Estruturação"));

    if (hasEdit) {
        return CanAccessDataResponse.edit(personData.getKey(), false, self);
    }

    // Regra UPDATE
    boolean hasUpdate = permissions.contains("UPDATE") || permissions.contains("EDIT");

    if (hasUpdate) {
        return CanAccessDataResponse.update(personData.getKey(), false, self);
    }

    // Regra READ
    boolean hasRead = permissions.contains("READ") || permissions.contains("EDIT");

    if (hasRead) {
        logger.info("tm: " + (System.currentTimeMillis() - tm));
        return CanAccessDataResponse.read(personData.getKey(), false, self);
    }
    
    
    return new CanAccessDataResponse(
      false,
      false,
      false,
      hasPermission(ids, personData.getKey(), this.permissionRepository::hasBasicReadPermission),
      false,
      self,
      personData.getKey(),
      new CanAccessManagementDataResponse(
        false,
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
