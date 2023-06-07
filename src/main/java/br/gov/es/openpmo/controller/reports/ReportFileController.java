package br.gov.es.openpmo.controller.reports;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.reports.models.DownloadReportModelFileResponse;
import br.gov.es.openpmo.dto.reports.models.ReportModelFileResponse;
import br.gov.es.openpmo.dto.reports.models.ReportModelRequest;
import br.gov.es.openpmo.service.reports.files.DeleteReportModelFileById;
import br.gov.es.openpmo.service.reports.files.DownloadReportModelFile;
import br.gov.es.openpmo.service.reports.files.UpdateReportModelFile;
import br.gov.es.openpmo.service.reports.files.UploadReportModelFile;
import io.swagger.annotations.Api;

@Api
@RestController
@RequestMapping("/report-model/files")
public class ReportFileController {

  private final UploadReportModelFile uploadReportModelFile;

  private final DownloadReportModelFile downloadReportModelFile;

  private final DeleteReportModelFileById deleteReportModelFileById;
  
  private final UpdateReportModelFile updateReportModelFile;

  public ReportFileController(
    final UploadReportModelFile uploadReportModelFile,
    final DownloadReportModelFile downloadReportModelFile,
    final DeleteReportModelFileById deleteReportModelFileById,
    final UpdateReportModelFile updateReportModelFile
  ) {
    this.uploadReportModelFile = uploadReportModelFile;
    this.downloadReportModelFile = downloadReportModelFile;
    this.deleteReportModelFileById = deleteReportModelFileById;
    this.updateReportModelFile = updateReportModelFile;
  }

  @PostMapping("/upload")
  public ResponseEntity<ResponseBase<ReportModelFileResponse>> upload(final MultipartFile file) {

    final ReportModelFileResponse response = this.uploadReportModelFile.execute(file);

    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @GetMapping("/{idFile}/download")
  public ResponseEntity<byte[]> download(
    @PathVariable final Long idFile,
    @RequestParam("id-report-model") final Long idReportModel
  ) {
    final DownloadReportModelFileResponse file = this.downloadReportModelFile.execute(idFile, idReportModel);
    return ResponseEntity.ok()
      .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
      .header(HttpHeaders.CONTENT_DISPOSITION, file.contentDisposition())
      .contentLength(file.getFileLength())
      .contentType(MediaType.APPLICATION_OCTET_STREAM)
      .body(file.getRawFile());
  }
  
  @Transactional
  @PatchMapping("/{idFile}/set-as-main")
  public ResponseEntity<ResponseBase<ReportModelFileResponse>> alterMainFile(
    @PathVariable final Long idFile,
    @RequestBody @Valid ReportModelRequest request
  ) {
	final ReportModelFileResponse response = this.updateReportModelFile.execute(idFile, request.getIdReportModel());
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @Transactional
  @DeleteMapping("/{idFile}")
  public ResponseEntity<ResponseBase<Void>> deleteById(@PathVariable final Long idFile) {
    this.deleteReportModelFileById.execute(idFile, true);
    return ResponseEntity.ok(ResponseBase.of());
  }

}
