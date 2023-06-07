package br.gov.es.openpmo.service.reports;

import br.gov.es.openpmo.dto.reports.GeneratedReport;
import br.gov.es.openpmo.dto.reports.ReportParamsRequest;
import br.gov.es.openpmo.dto.reports.ReportRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.repository.FileRepository;
import br.gov.es.openpmo.repository.LocalityRepository;
import br.gov.es.openpmo.repository.OrganizationRepository;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import br.gov.es.openpmo.repository.UnitMeasureRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.DocumentUtils;
import br.gov.es.openpmo.utils.JasperUtils;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GenerateReportComponent {

  private final ReportDesignRepository repository;

  private final FileRepository fileRepository;

  private final LocalityRepository localityRepository;

  private final OrganizationRepository organizationRepository;

  private final UnitMeasureRepository unitMeasureRepository;

  private final String fileReportPath;

  private final String urlConnection;

  private final String userName;

  private final String password;

  public GenerateReportComponent(
    final ReportDesignRepository repository,
    final FileRepository fileRepository,
    final LocalityRepository localityRepository,
    final OrganizationRepository organizationRepository,
    final UnitMeasureRepository unitMeasureRepository,
    @Value("${app.reportPath}") final String fileReportPath,
    @Value("${spring.data.neo4j.uri}") final String urlConnection,
    @Value("${spring.data.neo4j.username}") final String userName,
    @Value("${spring.data.neo4j.password}") final String password
  ) {
    this.repository = repository;
    this.fileRepository = fileRepository;
    this.localityRepository = localityRepository;
    this.organizationRepository = organizationRepository;
    this.unitMeasureRepository = unitMeasureRepository;
    this.fileReportPath = fileReportPath;
    this.urlConnection = urlConnection;
    this.userName = userName;
    this.password = password;
  }

  public GeneratedReport execute(final ReportRequest request) {
    JasperUtils jasperUtils = new JasperUtils();

    ReportDesign reportDesign = this.findByIdWithRelationships(request.getIdReportModel());

    File compiledFileMain = reportDesign.getCompiledSource().stream()
      .filter(File::getMain)
      .findFirst()
      .orElseThrow(() -> new NegocioException(ApplicationMessage.REPORT_DESIGN_MAIN_FILE_NOT_FOUND));

    final Path path = this.getPath(compiledFileMain.getUniqueNameKey());

    try {
      JasperReport jasperReportMain = JasperUtils.getJasperReportFromJasperFile(path.toFile());

      File jrxmlFileMain = this.fileRepository.findFileTemplateReportDesignWhereMainIsTrue(request.getIdReportModel()).get();

      JRDataSource dataSource = getDataSource(jrxmlFileMain, request.getScope());

      Map<String, Object> params = this.getParams(request);

      this.subReportsAsParams(reportDesign, params, request.getScope());

      byte[] reportBytes = jasperUtils.print(params, dataSource, request.getFormat(), jasperReportMain);

      GeneratedReport generatedReport = new GeneratedReport();

      generatedReport.setResource(this.convertToByteArrayResource(reportBytes));

      this.defineFileNameAndMediaType(generatedReport, request);

      return generatedReport;
    }
    catch (ClassNotFoundException | IOException | SQLException e) {
      e.printStackTrace();
      throw new NegocioException(ApplicationMessage.REPORT_GENERATE_ERROR);
    }
  }

  private ReportDesign findByIdWithRelationships(final Long idReportDesign) {
    return this.repository.findByIdWithRelationships(idReportDesign)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.REPORT_DESIGN_NOT_FOUND));
  }

  private void subReportsAsParams(ReportDesign reportDesign, Map<String, Object> params, List<Long> scope) {

    List<File> compiledSubReports = reportDesign.getCompiledSource().stream()
      .filter(f -> !f.getMain())
      .collect(Collectors.toList());

    for (File compiledSubReport : compiledSubReports) {
      try {
        final Path path = this.getPath(compiledSubReport.getUniqueNameKey());
        JasperReport jasperSubReport = JasperUtils.getJasperReportFromJasperFile(path.toFile());

        String userGivenName = compiledSubReport.getUserGivenName().substring(
          0,
          compiledSubReport.getUserGivenName().lastIndexOf(".")
        );

        File jrxmlSubReport = compiledSubReport.getTemplateFile();
        JRDataSource dataSourceToSubReport = this.getDataSource(jrxmlSubReport, scope);

        params.put(userGivenName, jasperSubReport);
        params.put("ds_" + userGivenName, dataSourceToSubReport);
      }
      catch (ClassNotFoundException | IOException | SQLException e) {
        e.printStackTrace();
        throw new NegocioException(ApplicationMessage.REPORT_GENERATE_ERROR);
      }
    }

  }

  private JRDataSource getDataSource(File jrxml, List<Long> scope) throws SQLException {
    Document doc = DocumentUtils.convertToXMLDocument(this.getPath(jrxml.getUniqueNameKey()));

    NodeList nodeList = doc.getDocumentElement().getChildNodes();

    Node node = null;
    String query = "";
    for (int i = 0; i < nodeList.getLength(); i++) {
      node = nodeList.item(i);
      if (node.getNodeName().equals("queryString")) {
        query = node.getChildNodes().item(1).getTextContent();
        break;
      }
    }

    if (StringUtils.isAllEmpty(query)) return new JREmptyDataSource();

    Connection connection = DriverManager.getConnection("jdbc:neo4j:" + this.urlConnection + "/neo4j", this.userName, this.password);

    final String joinedScope = scope.stream()
      .map(Object::toString)
      .collect(Collectors.joining(","));
    String builder = "MATCH (data)\n" +
                     "WHERE id(data) IN [ " + joinedScope + " ]\n" +
                     "WITH *\n" +
                     query;

    ResultSet rs = connection.createStatement().executeQuery(builder);
    connection.close();

    JRDataSource resultSetDataSource = new JRResultSetDataSource(rs);
    return resultSetDataSource;
  }

  private Map<String, Object> getParams(ReportRequest request) {
    Map<String, Object> parametros = new HashMap<>();
    List<ReportParamsRequest> params = request.getParams();
    if (params != null) params.forEach(param -> parametros.put(param.getName(), this.getValue(param)));
    return parametros;
  }

  private Object getValue(ReportParamsRequest param) {
    switch (param.getType()) {
      case "Currency":
        return new BigDecimal(param.getValue());
      case "Integer":
        return Integer.valueOf(param.getValue());
      case "Date":
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        LocalDateTime dateTime = LocalDateTime.parse(param.getValue(), formatter);
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
      case "Toggle":
        return BooleanUtils.toBoolean(param.getValue());
      case "LocalitySelection":
        List<String> localitiesNames = new ArrayList<>();
        Iterable<Locality> localities = this.localityRepository.findAllById(param.getSelectedValues(), 0);
        localities.forEach(locality -> localitiesNames.add(locality.getName()));
        return localitiesNames;
      case "OrganizationSelection":
        List<String> organizationsNames = new ArrayList<>();
        Iterable<Organization> organizations = this.organizationRepository.findAllById(param.getSelectedValues());
        organizations.forEach(organization -> organizationsNames.add(organization.getName()));
        return organizationsNames;
      case "UnitSelection":
        UnitMeasure unitMeasure = this.unitMeasureRepository.findById(param.getSelectedValue(), 0).get();
        return unitMeasure.getName();
      default:
        return param.getValue();
    }
  }

  private ByteArrayResource convertToByteArrayResource(byte[] report) {
    return new ByteArrayResource(report);
  }

  private void defineFileNameAndMediaType(GeneratedReport generatedReport, ReportRequest request) {
    MediaType contentType = null;
    String fileName = null;
    switch (request.getFormat()) {
      case PDF:
        contentType = MediaType.parseMediaType("application/pdf");
        fileName = "Relatorio.pdf";
        break;
      case HTML:
        contentType = MediaType.parseMediaType("text/html");
        fileName = "Relatorio.html";
        break;
      case ODT:
        contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        fileName = "Relatorio.docx";
        break;
      case XLS:
        contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        fileName = "Relatorio.xlsx";
        break;
      case CSV:
        contentType = MediaType.parseMediaType("text/csv");
        fileName = "Relatorio.csv";
        break;
      case RTF:
        contentType = MediaType.parseMediaType("application/rtf");
        fileName = "Relatorio.rtf";
        break;
      case XML:
        contentType = MediaType.parseMediaType("application/xml");
        fileName = "Relatorio.xml";
        break;
      case TXT:
        contentType = MediaType.parseMediaType("text/plain");
        fileName = "Relatorio.txt";
        break;
      default:
        throw new NegocioException(ApplicationMessage.REPORT_GENERATE_UNKNOWN_TYPE_ERROR);
    }
    generatedReport.setContentType(contentType);
    generatedReport.setFilename(fileName);
  }

  private Path getPath(final String uniqueNameKey) {
    return Paths.get(this.fileReportPath + uniqueNameKey);
  }

}
