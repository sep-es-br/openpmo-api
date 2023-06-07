package br.gov.es.openpmo.service.reports.files;

import br.gov.es.openpmo.dto.reports.models.DownloadReportModelFileResponse;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.repository.FileRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DownloadReportModelFile {

  private static final Logger log = LoggerFactory.getLogger(DownloadReportModelFile.class);

  private final FileRepository fileRepository;

  private final String fileReportPath;

  public DownloadReportModelFile(
    final FileRepository fileRepository,
    @Value("${app.reportPath}") final String fileReportPath
  ) {
    this.fileRepository = fileRepository;
    this.fileReportPath = fileReportPath;
  }

  public DownloadReportModelFileResponse execute(final Long idFile, final Long idReportModel) {
    try {

      log.info("Iniciando download do arquivo fileId={} e reportDesignId={}", idFile, idReportModel);

      final File file = this.getReportFileTemplate(idFile, idReportModel);

      log.debug("Recuperando o caminho do arquivo fileId={}", idFile);
      final Path path = this.getPath(file.getUniqueNameKey());

      if (Files.notExists(path)) {
        log.debug("Caminho não encontrado para fileId={} path={}", idFile, path.toAbsolutePath());
        throw new RegistroNaoEncontradoException(ApplicationMessage.FILE_NOT_FOUND);
      }
      log.info("Caminho encontrado para fileId={} path={} retornando o arquivo", idFile, path.toAbsolutePath());

      final byte[] fileAsBytes = Files.readAllBytes(path);

      return new DownloadReportModelFileResponse(fileAsBytes, file.getUserGivenName());
    }
    catch (final IOException e) {
      log.error(
        "Ocorreu um erro ao realizar o download do arquivo idFile={} relacionado ao idReportModel={}",
        idFile,
        idReportModel,
        e
      );
      throw new RuntimeException(e);
    }
  }

  private Path getPath(final String uniqueNameKey) {
    return Paths.get(this.fileReportPath + uniqueNameKey);
  }

  private File getReportFileTemplate(final Long idFile, final Long idReportModel) {
    return this.fileRepository.findFileTemplateByIdAndReportDesign(idFile, idReportModel)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.FILE_NOT_FOUND));
  }

}
