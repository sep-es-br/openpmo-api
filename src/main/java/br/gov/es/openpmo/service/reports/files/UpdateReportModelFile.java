package br.gov.es.openpmo.service.reports.files;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.gov.es.openpmo.dto.reports.models.ReportModelFileResponse;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.repository.FileRepository;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Component
public class UpdateReportModelFile {

  private final FileRepository fileRepository;
  
  private final ReportDesignRepository reportDesignRepository;

  public UpdateReportModelFile(
    final FileRepository fileRepository,
    final ReportDesignRepository reportDesignRepository
  ) {
    this.fileRepository = fileRepository;
    this.reportDesignRepository = reportDesignRepository;
  }

  public ReportModelFileResponse execute(final Long idFile, final Long idReportModel) {
	Optional<File> fileMainCurrentOptional = this.getReportFileTemplateWhereMainIsTrue(idReportModel);
	if (fileMainCurrentOptional.isPresent()) {
		File fileMainCurrent = fileMainCurrentOptional.get();
		fileMainCurrent.setMain(Boolean.FALSE);
		fileRepository.save(fileMainCurrent, 0);
	}
	
	File fileMainNewMain = this.getReportFileTemplate(idFile, idReportModel);
	fileMainNewMain.setMain(Boolean.TRUE);
	fileRepository.save(fileMainNewMain, 0);
	
	Optional<ReportDesign> reportDesignOptional = reportDesignRepository.findById(idReportModel);
	if (reportDesignOptional.isPresent()) {
		ReportDesign reportDesign = reportDesignOptional.get();
		reportDesign.setActive(Boolean.FALSE);
		reportDesignRepository.save(reportDesign, 0);
	}
	
	return ReportModelFileResponse.of(fileMainNewMain.getUserGivenName(), fileMainNewMain.getUniqueNameKey(),
			fileMainNewMain.getMimeType());	
  }
  
  private Optional<File> getReportFileTemplateWhereMainIsTrue(final Long idReportModel) {
    return this.fileRepository.findFileTemplateReportDesignWhereMainIsTrue(idReportModel);
  }
  
  private File getReportFileTemplate(final Long idFile, final Long idReportModel) {
    return this.fileRepository.findFileTemplateByIdAndReportDesign(idFile, idReportModel)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.FILE_NOT_FOUND));
  }

}
