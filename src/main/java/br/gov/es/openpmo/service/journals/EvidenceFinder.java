package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.text.MessageFormat;

import static br.gov.es.openpmo.utils.ApplicationMessage.FILE_NOT_FOUND;

@Service
public class EvidenceFinder {

  private final FileRepository fileRepository;

  @Value("${app.journalPath}")
  private String basePath;

  @Autowired
  public EvidenceFinder(final FileRepository fileRepository) {
    this.fileRepository = fileRepository;
  }

  public UrlResource getEvidence(final Long idEvidence) throws IOException {
    final File evidence = this.findEvidenceById(idEvidence);
    final String path = this.getPath(evidence);
    final URI uri = Paths.get(path).toUri();
    return new UrlResource(uri);
  }

  private String getPath(final File evidence) {
    return MessageFormat.format("{0}{1}", this.basePath, evidence.getUniqueNameKey());
  }

  private File findEvidenceById(final Long idEvidence) {
    return this.fileRepository.findById(idEvidence)
      .orElseThrow(() -> new NegocioException(FILE_NOT_FOUND));
  }

}
