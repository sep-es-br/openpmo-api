package br.gov.es.openpmo.controller.actor;

import br.gov.es.openpmo.dto.ComboDto;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.person.*;
import br.gov.es.openpmo.dto.person.detail.PersonDetailDto;
import br.gov.es.openpmo.enumerator.CcbMemberFilterEnum;
import br.gov.es.openpmo.enumerator.StakeholderFilterEnum;
import br.gov.es.openpmo.enumerator.UserFilterEnum;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.permissions.PersonPermissionsService;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Api
@RestController
@CrossOrigin
@RequestMapping("/persons")
public class PersonController {

  private final PersonService personService;
  private final PersonPermissionsService personPermissionsService;
  private final ResponseHandler responseHandler;

  @Autowired
  public PersonController(
    final PersonService personService,
    final PersonPermissionsService personPermissionsService,
    ResponseHandler responseHandler
  ) {
    this.personService = personService;
    this.personPermissionsService = personPermissionsService;
    this.responseHandler = responseHandler;
  }

  @GetMapping("/{key}")
  public ResponseEntity<ResponseBase<PersonGetByIdDto>> findByKey(
    @PathVariable final String key,
    @RequestParam(required = false) final Long idOffice,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    final Optional<PersonGetByIdDto> maybePerson =
      this.personService.maybeFindPersonDataByKey(key, idOffice, uriComponentsBuilder);

    if (!maybePerson.isPresent()) {
      return ResponseEntity.noContent().build();
    }

    final ResponseBase<PersonGetByIdDto> response = new ResponseBase<PersonGetByIdDto>()
      .setData(maybePerson.get())
      .setSuccess(true);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/office/{officeScope}")
  public ResponseEntity<ResponseBase<List<PersonListDto>>> findAll(
    @PathVariable final Long officeScope,
    @RequestParam(value = "stakeholderStatus", required = false, defaultValue = "ALL") final StakeholderFilterEnum stakeholderStatus,
    @RequestParam(value = "userStatus", required = false, defaultValue = "ALL") final UserFilterEnum userStatus,
    @RequestParam(value = "ccbMemberStatus", required = false, defaultValue = "ALL") final CcbMemberFilterEnum ccbMemberStatus,
    @RequestParam(value = "name", required = false) final String name,
    @RequestParam(value = "planScope", required = false) final Long[] planScope,
    @RequestParam(value = "workpackScope", required = false) final Long[] workpackScope,
    final UriComponentsBuilder uriComponentsBuilder
  ) {

    final List<PersonListDto> persons = this.personService.findAll(
      stakeholderStatus,
      userStatus,
      ccbMemberStatus,
      name,
      officeScope,
      planScope,
      workpackScope,
      uriComponentsBuilder
    );

    final ResponseBase<List<PersonListDto>> response = new ResponseBase<List<PersonListDto>>()
      .setData(persons)
      .setSuccess(true);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{personId}/office/{officeId}")
  public ResponseEntity<ResponseBase<PersonDetailDto>> findById(
    @PathVariable("personId") final Long personId,
    @PathVariable("officeId") final Long officeId,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    final PersonDetailDto personDetailDto = this.personService.findPersonDetailsById(
      personId,
      officeId,
      uriComponentsBuilder
    );

    final ResponseBase<PersonDetailDto> response = new ResponseBase<PersonDetailDto>()
      .setData(personDetailDto)
      .setSuccess(true);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/office")
  public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid final PersonUpdateDto personUpdateDto) {
    final Person person = this.personService.update(personUpdateDto);

    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>()
      .setData(new EntityDto(person.getId()))
      .setSuccess(true);

    return ResponseEntity.ok().body(entity);
  }

  @DeleteMapping("/{idPerson}/office/{idOffice}/permissions")
  public ResponseEntity<ResponseBase<Void>> deleteAllPermissions(
    @PathVariable final Long idPerson,
    @PathVariable final Long idOffice
  ) {
    this.personPermissionsService.deleteAllPermissions(idPerson, idOffice);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/fullname")
  public ResponseEntity<ResponseBase<List<PersonDto>>> findPersonByFullname(
    @RequestParam("fullName") final String partialName,
    @RequestParam final Long idWorkpack
  ) {
    final List<PersonDto> persons =
      this.personService.findPersonsByFullNameAndWorkpack(partialName, idWorkpack);

    if (persons == null) {
      return ResponseEntity.noContent().build();
    }

    final ResponseBase<List<PersonDto>> response = new ResponseBase<List<PersonDto>>()
      .setData(persons)
      .setSuccess(true);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{idPerson}/offices")
  public ResponseEntity<ResponseBase<List<ComboDto>>> findAllOfficesByPerson(@PathVariable final Long idPerson) {
    final List<ComboDto> offices = this.personService.findOfficesByPersonId(idPerson);
    final ResponseBase<List<ComboDto>> response = new ResponseBase<List<ComboDto>>()
      .setData(offices)
      .setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody final PersonCreateRequest request) {
    final Person person = this.personService.create(request);
    final ResponseBase<EntityDto> response = new ResponseBase<EntityDto>()
      .setData(new EntityDto(person.getId()))
      .setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @Transactional
  @PatchMapping("/{id-person}")
  public Response<Void> updateName(
    @PathVariable("id-person") Long idPerson,
    @RequestBody NameRequest nameRequest
  ) {
    this.personService.updateName(idPerson, nameRequest.getName());
    return responseHandler.success();
  }

}
