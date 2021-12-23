package br.gov.es.openpmo.service.permissions;

import br.gov.es.openpmo.dto.person.queries.AllPersonPermissionQuery;
import br.gov.es.openpmo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonPermissionsService {

  private final OfficePermissionService officePermissionService;
  private final WorkpackPermissionService workpackPermissionService;
  private final PlanPermissionService planPermissionService;

  private final PersonRepository personRepository;

  @Autowired
  public PersonPermissionsService(
    final OfficePermissionService officePermissionService,
    final WorkpackPermissionService workpackPermissionService,
    final PlanPermissionService planPermissionService,
    final PersonRepository personRepository
  ) {
    this.officePermissionService = officePermissionService;
    this.workpackPermissionService = workpackPermissionService;
    this.planPermissionService = planPermissionService;
    this.personRepository = personRepository;
  }

  @Transactional
  public void deleteAllPermissions(final Long idPerson, final Long idOffice) {

    final AllPersonPermissionQuery allPersonPermission = this.personRepository.findAllPermissionBy(
      idPerson,
      idOffice
    );

    this.officePermissionService.deleteAll(allPersonPermission.getCanAccessOffice());
    this.planPermissionService.deleteAll(allPersonPermission.getCanAccessPlan());
    this.workpackPermissionService.deleteAll(allPersonPermission.getCanAccessWorkpack());
  }


}
