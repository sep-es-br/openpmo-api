package br.gov.es.openpmo.controller.actor;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ComboDto;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBasePaginated;
import br.gov.es.openpmo.dto.actor.PersonListFilterParameters;
import br.gov.es.openpmo.dto.person.LocalWorkRequest;
import br.gov.es.openpmo.dto.person.NameRequest;
import br.gov.es.openpmo.dto.person.PersonCreateRequest;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.dto.person.PersonGetByIdDto;
import br.gov.es.openpmo.dto.person.PersonListDto;
import br.gov.es.openpmo.dto.person.PersonUpdateDto;
import br.gov.es.openpmo.dto.person.detail.PersonDetailDto;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.permissions.PersonPermissionsService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collections;
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

  private final ICanAccessService canAccessService;
  private final TokenService tokenService;

  @Autowired
  public PersonController(
    final PersonService personService,
    final PersonPermissionsService personPermissionsService,
    final ResponseHandler responseHandler,
    final ICanAccessService canAccessService,
    final TokenService tokenService
  ) {
    this.personService = personService;
    this.personPermissionsService = personPermissionsService;
    this.responseHandler = responseHandler;
    this.canAccessService = canAccessService;
    this.tokenService = tokenService;
  }

  @GetMapping("/{key}")
  public ResponseEntity<ResponseBase<PersonGetByIdDto>> findByKey(
    @PathVariable final String key,
    @RequestParam(required = false) final Long idOffice,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    final Optional<PersonGetByIdDto> maybePerson = this.personService.maybeFindPersonDataByKey(key, idOffice,
                                                                                               uriComponentsBuilder
    );

    return maybePerson
      .map(personGetByIdDto -> ResponseEntity.ok(ResponseBase.of(personGetByIdDto)))
      .orElseGet(() -> ResponseEntity.noContent().build());
  }

  @PostMapping("/office/{officeScope}")
  public ResponseEntity<ResponseBasePaginated<PersonListDto>> findAll(
    @PathVariable final Long officeScope,
    @RequestBody final PersonListFilterParameters parameters,
    final UriComponentsBuilder uriComponentsBuilder,
    final Pageable pageable,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanAccessManagementResource(officeScope, authorization);

    final Page<PersonListDto> persons = this.personService.findAll(
      parameters.getStakeholderStatus(),
      parameters.getUserStatus(),
      parameters.getCcbMemberStatus(),
      parameters.getName(),
      parameters.getScope(),
      pageable,
      uriComponentsBuilder
    );

    return ResponseEntity.ok(ResponseBasePaginated.of(persons));
  }

  @GetMapping("/{personId}/office/{officeId}")
  public ResponseEntity<ResponseBase<PersonDetailDto>> findById(
    @PathVariable("personId") final Long personId,
    @PathVariable("officeId") final Long officeId,
    final UriComponentsBuilder uriComponentsBuilder,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanAccessManagementOrSelfResource(Arrays.asList(personId, officeId), authorization);
    final PersonDetailDto personDetailDto = this.personService.findPersonDetailsById(
      personId,
      officeId,
      uriComponentsBuilder
    );
    return ResponseEntity.ok(ResponseBase.of(personDetailDto));
  }

  @PutMapping("/office")
  public ResponseEntity<ResponseBase<EntityDto>> update(
    @RequestBody @Valid final PersonUpdateDto personUpdateDto,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanAccessManagementOrSelfResource(
      Arrays.asList(personUpdateDto.getId(), personUpdateDto.getIdOffice()),
      authorization
    );
    final Person person = this.personService.update(personUpdateDto);
    return ResponseEntity.ok().body(ResponseBase.of(new EntityDto(person.getId())));
  }

  @DeleteMapping("/{idPerson}/office/{idOffice}/permissions")
  public ResponseEntity<ResponseBase<Void>> deleteAllPermissions(
    @PathVariable final Long idPerson,
    @PathVariable final Long idOffice,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanAccessManagementResource(idPerson, authorization);
    this.personPermissionsService.deleteAllPermissions(idPerson, idOffice);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/fullname")
  public ResponseEntity<ResponseBase<List<PersonDto>>> findPersonByFullname(
    @RequestParam("fullName") final String partialName,
    @RequestParam final Long idWorkpack
  ) {

    final List<PersonDto> persons = this.personService.findPersonsByFullNameAndWorkpack(partialName, idWorkpack);

    return persons == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(ResponseBase.of(persons));
  }

  @GetMapping("/{idPerson}/offices")
  public ResponseEntity<ResponseBase<List<ComboDto>>> findAllOfficesByPerson(
    @PathVariable final Long idPerson,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanAccessManagementOrSelfResource(Collections.singletonList(idPerson), authorization);
    final List<ComboDto> offices = this.personService.findOfficesByPersonId(idPerson);
    return ResponseEntity.ok(ResponseBase.of(offices));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(
    @Valid @RequestBody final PersonCreateRequest request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureIsAdministrator(authorization);
    final Person person = this.personService.create(request);
    return ResponseEntity.ok(ResponseBase.of(new EntityDto(person.getId())));
  }

  @Transactional
  @PatchMapping("/{id-person}")
  public Response<Void> updateName(
    @PathVariable("id-person") final Long idPerson,
    @RequestBody @Valid final NameRequest nameRequest,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(idPerson, authorization);
    this.personService.updateName(idPerson, nameRequest.getName());
    return this.responseHandler.success();
  }

  @Transactional
  @PatchMapping("/work-local")
  public Response<Void> updateLocalWork(
    @RequestBody final LocalWorkRequest request,
    @Authorization final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    this.personService.updateLocalWork(idPerson, request);
    return this.responseHandler.success();
  }

}
