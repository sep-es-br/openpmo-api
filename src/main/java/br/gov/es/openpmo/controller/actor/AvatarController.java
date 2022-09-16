package br.gov.es.openpmo.controller.actor;


import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.file.AvatarDto;
import br.gov.es.openpmo.service.actors.AvatarService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;

@Api
@RestController
@RequestMapping
public class AvatarController {

  private final AvatarService service;
  private final ICanAccessService canAccessService;

  @Autowired
  public AvatarController(
    final AvatarService service,
    final ICanAccessService canAccessService
  ) {
    this.service = service;
    this.canAccessService = canAccessService;
  }

  @GetMapping("/avatar/{idAvatar}/person")
  public ResponseEntity<UrlResource> getAvatar(@PathVariable final Long idAvatar) throws IOException {
    final UrlResource imagem = this.service.getAvatar(idAvatar);
    return ResponseEntity.status(HttpStatus.OK)
      .contentType(MediaTypeFactory
                     .getMediaType(imagem)
                     .orElse(MediaType.APPLICATION_OCTET_STREAM))
      .body(imagem);
  }


  @GetMapping("/persons/{idPerson}/avatar")
  public ResponseEntity<ResponseBase<AvatarDto>> findById(
    @PathVariable("idPerson") final Long idPerson,
    @RequestParam(value = "id-office", required = false) final Long idOffice,
    @Authorization final String authorization,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    this.canAccessService.ensureCanAccessManagementOrSelfResource(Arrays.asList(idOffice, idPerson), authorization);
    final AvatarDto avatar = this.service.findById(idPerson, uriComponentsBuilder);
    return ResponseEntity.ok(ResponseBase.of(avatar));
  }

  @PostMapping("/persons/{idPerson}/avatar")
  public ResponseEntity<ResponseBase<AvatarDto>> upload(
    @PathVariable final Long idPerson,
    @RequestParam final MultipartFile file,
    @RequestParam(value = "id-office", required = false) final Long idOffice,
    @Authorization final String authorization,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    this.canAccessService.ensureCanAccessManagementOrSelfResource(Arrays.asList(idOffice, idPerson), authorization);
    final AvatarDto avatarDto = this.service.create(file, idPerson, uriComponentsBuilder);
    return ResponseEntity.ok(ResponseBase.of(avatarDto));
  }

  @PutMapping("/persons/{idPerson}/avatar")
  public ResponseEntity<ResponseBase<AvatarDto>> update(
    @PathVariable final Long idPerson,
    @RequestParam final MultipartFile file,
    @RequestParam(value = "id-office", required = false) final Long idOffice,
    @Authorization final String authorization,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    this.canAccessService.ensureCanAccessManagementOrSelfResource(Arrays.asList(idPerson, idOffice), authorization);
    final AvatarDto avatarDto = this.service.update(file, idPerson, uriComponentsBuilder);
    return ResponseEntity.ok(ResponseBase.of(avatarDto));
  }

  @DeleteMapping("/persons/{idPerson}/avatar")
  public ResponseEntity<Void> delete(
    @PathVariable final Long idPerson,
    @RequestParam(value = "id-office", required = false) final Long idOffice,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanAccessManagementOrSelfResource(Arrays.asList(idPerson, idOffice), authorization);
    this.service.deleteAvatarByIdPerson(idPerson);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

}
