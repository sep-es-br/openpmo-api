package br.gov.es.openpmo.controller.journals;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.service.journals.EvidenceCreator;
import br.gov.es.openpmo.service.journals.EvidenceFinder;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api
@RestController
@RequestMapping("/evidence")
public class EvidenceController {

  private final EvidenceFinder evidenceFinder;

  private final EvidenceCreator evidenceCreator;

  private final ResponseHandler responseHandler;

  private final ICanAccessService canAccessService;

  @Autowired
  public EvidenceController(
      final EvidenceFinder evidenceFinder,
      final EvidenceCreator evidenceCreator,
      final ResponseHandler responseHandler,
      final ICanAccessService canAccessService) {
    this.evidenceFinder = evidenceFinder;
    this.evidenceCreator = evidenceCreator;
    this.responseHandler = responseHandler;
    this.canAccessService = canAccessService;
  }

  private static MediaType getContentType(final Resource imagem) {
    return MediaTypeFactory.getMediaType(imagem)
        .orElse(MediaType.APPLICATION_OCTET_STREAM);
  }

  @GetMapping("/image/{id-evidence}")
  public ResponseEntity<UrlResource> getEvidence(@PathVariable("id-evidence") final Long idEvidence,
      @Authorization final String authorization)
      throws IOException {

    this.canAccessService.ensureCanReadResource(idEvidence, authorization);
    final UrlResource imagem = this.evidenceFinder.getEvidence(idEvidence);

    return ResponseEntity.ok()
        .contentType(getContentType(imagem))
        .body(imagem);
  }

  @Transactional
  @PostMapping("/{id-journal}")
  public Response<Void> create(
      @PathVariable("id-journal") final Long idJournal,
      @RequestParam final MultipartFile file,
      @Authorization final String authorization) throws IOException {

    this.canAccessService.ensureCanEditResource(idJournal, authorization);
    this.evidenceCreator.create(idJournal, file);
    return this.responseHandler.success();
  }

}
