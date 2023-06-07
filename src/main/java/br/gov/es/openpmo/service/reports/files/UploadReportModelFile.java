package br.gov.es.openpmo.service.reports.files;

import br.gov.es.openpmo.dto.reports.models.ReportModelFileResponse;
import br.gov.es.openpmo.service.files.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadReportModelFile {

  private final FileService fileService;

  private final String fileReportPath;

  public UploadReportModelFile(
    final FileService fileService,
    @Value("${app.reportPath}") final String fileReportPath
  ) {
    this.fileService = fileService;
    this.fileReportPath = fileReportPath;
  }

  public ReportModelFileResponse execute(final MultipartFile file) {

    final String uniqueNameKey = this.fileService.generateName(file);

    this.fileService.save(file, uniqueNameKey, this.fileReportPath);

    return ReportModelFileResponse.of(file, uniqueNameKey);
  }

}
