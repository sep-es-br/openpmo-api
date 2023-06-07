package br.gov.es.openpmo.service.reports.files;

import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.repository.FileRepository;
import br.gov.es.openpmo.service.files.FileService;
import br.gov.es.openpmo.utils.ApplicationMessage;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class DeleteReportModelFileById {

  private static final Logger log = LoggerFactory.getLogger(DeleteReportModelFileById.class);

  private final FileService fileService;

  private final FileRepository fileRepository;

  private final String fileReportPath;

  public DeleteReportModelFileById(
    final FileService fileService,
    final FileRepository fileRepository,
    @Value("${app.reportPath}") final String fileReportPath
  ) {
    this.fileService = fileService;
    this.fileRepository = fileRepository;
    this.fileReportPath = fileReportPath;
  }

  public void execute(@NonNull final Long idFile) {
	this.execute(idFile, false);
  }

  public void execute(@NonNull final Long idFile, final boolean compiledFileToo) {
    final File file = this.getFile(idFile);
    if (compiledFileToo) {
	  Optional<File> compiledFileOptional = this.fileRepository.findCompiledFileByTemplateFileId(idFile);
      if (compiledFileOptional.isPresent()) {
    	this.removeFile(compiledFileOptional.get());
      }
    }
    this.removeFile(file);
  }
  
  private void removeFile(final File file) {
	log.info("Removendo registro de fileId={}", file.getId());
	this.fileRepository.deleteById(file.getId());
	log.info("Registro de fileId={} removido com sucesso", file.getId());

	this.fileService.remove(this.fileReportPath.concat(file.getUniqueNameKey()));
	log.info("Registro {} de File removido com sucesso do sistema de arquivos", file.getId());
  }

  private File getFile(final Long idFile) {
    log.debug("Consultando idFile={}", idFile);
    return this.fileRepository.findById(idFile)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.FILE_NOT_FOUND));
  }

}
