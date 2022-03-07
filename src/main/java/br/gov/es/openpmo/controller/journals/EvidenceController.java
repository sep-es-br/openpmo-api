package br.gov.es.openpmo.controller.journals;

import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.journals.EvidenceCreator;
import br.gov.es.openpmo.service.journals.EvidenceFinder;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api
@RestController
@RequestMapping("/evidence")
public class EvidenceController {

  private final EvidenceFinder evidenceFinder;

  private final EvidenceCreator evidenceCreator;

  private final TokenService tokenService;

  private final ResponseHandler responseHandler;

  @Autowired
  public EvidenceController(
      final EvidenceFinder evidenceFinder,
      final EvidenceCreator evidenceCreator,
      final TokenService tokenService,
      final ResponseHandler responseHandler
  ) {
    this.evidenceFinder = evidenceFinder;
    this.evidenceCreator = evidenceCreator;
    this.tokenService = tokenService;
    this.responseHandler = responseHandler;
  }

  @GetMapping("/image/{id-evidence}")
  public ResponseEntity<UrlResource> getEvidence(@PathVariable("id-evidence") final Long idEvidence) throws IOException {
    final UrlResource imagem = this.evidenceFinder.getEvidence(idEvidence);

    return ResponseEntity.ok()
        .contentType(getContentType(imagem))
        .body(imagem);
  }

  private static MediaType getContentType(final Resource imagem) {
    return MediaTypeFactory.getMediaType(imagem)
        .orElse(MediaType.APPLICATION_OCTET_STREAM);
  }

  @Transactional
  @PostMapping("/{id-journal}")
  public Response<Void> create(
      @PathVariable("id-journal") final Long idJournal,
      @RequestParam final MultipartFile file,
      @RequestHeader(name = "Authorization") final String authorization
  ) throws IOException {
    final Long idPerson = this.tokenService.getUserId(authorization);
    this.evidenceCreator.create(idJournal, idPerson, file);
    return this.responseHandler.success();
  }

}
