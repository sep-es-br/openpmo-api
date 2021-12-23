package br.gov.es.openpmo.controller.journals;

import br.gov.es.openpmo.service.journals.evidences.EvidenceFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/journal")
public class EvidenceController {

  private final EvidenceFinder evidenceFinder;

  @Autowired
  public EvidenceController(final EvidenceFinder evidenceFinder) {
    this.evidenceFinder = evidenceFinder;
  }

  @GetMapping("/evidence/{idEvidence}")
  public ResponseEntity<UrlResource> getEvidence(@PathVariable final Long idEvidence) throws IOException {
    final UrlResource imagem = this.evidenceFinder.getEvidence(idEvidence);

    return ResponseEntity.ok()
      .contentType(getContentType(imagem))
      .body(imagem);
  }

  private static MediaType getContentType(final Resource imagem) {
    return MediaTypeFactory.getMediaType(imagem)
      .orElse(MediaType.APPLICATION_OCTET_STREAM);
  }

}
