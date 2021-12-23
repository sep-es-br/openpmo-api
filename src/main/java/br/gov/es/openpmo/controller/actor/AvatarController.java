package br.gov.es.openpmo.controller.actor;


import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.file.AvatarDto;
import br.gov.es.openpmo.service.actors.AvatarService;
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

@Api
@RestController
@RequestMapping
public class AvatarController {

  private final AvatarService service;

  @Autowired
  public AvatarController(final AvatarService service) {
    this.service = service;
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
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    final AvatarDto avatar = this.service.findById(idPerson, uriComponentsBuilder);

    final ResponseBase<AvatarDto> response = new ResponseBase<AvatarDto>()
      .setData(avatar)
      .setSuccess(true);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/persons/{idPerson}/avatar")
  public ResponseEntity<ResponseBase<AvatarDto>> upload(
    @PathVariable final Long idPerson,
    @RequestParam final MultipartFile file,
    final UriComponentsBuilder uriComponentsBuilder
  ) throws IOException {
    final AvatarDto avatarDto = this.service.save(file, idPerson, uriComponentsBuilder);

    final ResponseBase<AvatarDto> response = new ResponseBase<AvatarDto>()
      .setData(avatarDto)
      .setSuccess(true);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping("/persons/{idPerson}/avatar")
  public ResponseEntity<ResponseBase<AvatarDto>> update(
    @PathVariable final Long idPerson,
    @RequestParam final MultipartFile file,
    final UriComponentsBuilder uriComponentsBuilder
  ) throws IOException {
    final AvatarDto avatarDto = this.service.update(file, idPerson, uriComponentsBuilder);

    final ResponseBase<AvatarDto> response = new ResponseBase<AvatarDto>()
      .setData(avatarDto)
      .setSuccess(true);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/persons/{idPerson}/avatar")
  public ResponseEntity<Void> delete(@PathVariable final Long idPerson) {
    this.service.deleteAvatarByIdPerson(idPerson);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
