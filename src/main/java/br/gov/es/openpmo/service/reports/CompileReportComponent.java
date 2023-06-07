package br.gov.es.openpmo.service.reports;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.repository.FileRepository;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import br.gov.es.openpmo.service.files.FileService;
import br.gov.es.openpmo.service.reports.files.DeleteReportModelFileById;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.JasperUtils;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class CompileReportComponent {

  private final DeleteReportModelFileById deleteReportModelFileById;

  private final ReportDesignRepository repository;

  private final FileService fileService;

  private final FileRepository fileRepository;

  private final String fileReportPath;


  public CompileReportComponent(
    final DeleteReportModelFileById deleteReportModelFileById,
    final ReportDesignRepository repository,
    final FileService fileService,
    final FileRepository fileRepository,
    @Value("${app.reportPath}") final String fileReportPath
  ) {
    this.deleteReportModelFileById = deleteReportModelFileById;
    this.repository = repository;
    this.fileService = fileService;
    this.fileRepository = fileRepository;
    this.fileReportPath = fileReportPath;
  }

  public void execute(final Long idReportDesign) {
    final ReportDesign reportDesign = this.findByIdWithRelationships(idReportDesign);
    Set<File> templateSource = reportDesign.getTemplateSource();
    this.verifyQuantityMain(templateSource);
    Set<File> compiledSourceNew = this.compile(templateSource);

    // se tudo compilado, excluir os compilados anteriormente
    Set<File> compiledSourceOld = reportDesign.getCompiledSource();
    if (compiledSourceOld != null) {
      compiledSourceOld.forEach(file -> this.deleteReportModelFileById.execute(file.getId()));
      compiledSourceOld.clear();
    }

    // se tudo compilado, ATIVO=TRUE de reportdesign
    reportDesign.setActive(Boolean.TRUE);
    reportDesign.setCompiledSource(compiledSourceNew);
    this.repository.save(reportDesign);
  }

  private Set<File> compile(Set<File> templateSource) {
    Set<File> compiledSource = new HashSet<>();
    Map<File, OutputStream> jasperMap = new HashMap<>();
    for (File file : templateSource) {
      final Path path = this.getPath(file.getUniqueNameKey());
      if (Files.notExists(path)) {
        throw new RegistroNaoEncontradoException(ApplicationMessage.FILE_NOT_FOUND);
      }
      try {
        OutputStream jasper = JasperUtils.compileJrxml(path.toFile());
        jasperMap.put(file, jasper);
      }
      catch (JRException | IOException e) {
        e.printStackTrace();
        throw new RegistroNaoEncontradoException(ApplicationMessage.REPORT_DESIGN_COMPILED_ERROR);
      }
    }

    jasperMap.forEach((k, v) -> {
      String userGivenName = k.getUserGivenName().substring(0, k.getUserGivenName().lastIndexOf("."));
      userGivenName = userGivenName + ".jasper";
      InputStream inputStream = this.convertOutputToInputStream(v);
      final String uniqueNameKey = this.fileService.generateName(userGivenName);
      this.fileService.save(inputStream, uniqueNameKey, this.fileReportPath);
      try {
        v.close();
        inputStream.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      File newFile = new File();
      newFile.setId(null);
      newFile.setPerson(null);
      newFile.setJournalEntry(null);
      newFile.setMain(k.getMain());
      newFile.setMimeType("application/octet-stream");
      newFile.setUniqueNameKey(uniqueNameKey);
      newFile.setUserGivenName(userGivenName);
      newFile.setTemplateFile(k);
      newFile = this.fileRepository.save(newFile, 0);
      compiledSource.add(newFile);
    });
    return compiledSource;
  }

  private InputStream convertOutputToInputStream(OutputStream outputStream) {
    byte[] bytes = ((ByteArrayOutputStream) outputStream).toByteArray();
    InputStream inputStream = new ByteArrayInputStream(bytes);
    return inputStream;
  }

  private Path getPath(final String uniqueNameKey) {
    return Paths.get(this.fileReportPath + uniqueNameKey);
  }

  private ReportDesign findByIdWithRelationships(final Long idReportDesign) {
    return this.repository.findByIdWithRelationships(idReportDesign)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.REPORT_DESIGN_NOT_FOUND));
  }

  private void verifyQuantityMain(Set<File> templateSource) {
    if (templateSource == null)
      throw new NegocioException(ApplicationMessage.REPORT_DESIGN_MAIN_FILE_TEMPLATE_QUANTITY_DIFFERENT_THAN_ONE);
    int quantMain = templateSource.stream().mapToInt(f -> f.getMain() ? 1 : 0).sum();
    if (quantMain != 1) throw new NegocioException(ApplicationMessage.REPORT_DESIGN_MAIN_FILE_TEMPLATE_QUANTITY_DIFFERENT_THAN_ONE);
  }

}
