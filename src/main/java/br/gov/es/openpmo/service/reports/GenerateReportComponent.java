package br.gov.es.openpmo.service.reports;

import br.gov.es.openpmo.dto.reports.GeneratedReport;
import br.gov.es.openpmo.dto.reports.ReportParamsRequest;
import br.gov.es.openpmo.dto.reports.ReportRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.model.properties.models.*;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.repository.LocalityRepository;
import br.gov.es.openpmo.repository.OrganizationRepository;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.JasperUtils;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.*;

@Component
public class GenerateReportComponent {

  private final ReportDesignRepository repository;
  private final LocalityRepository localityRepository;

  private final OrganizationRepository organizationRepository;

  private final ReportScopeValidator reportScopeValidator;

  private final PropertyModelRepository propertyModelRepository;

  private final String fileReportPath;

  private final String urlConnection;

  private final String userName;

  private final String password;

  public GenerateReportComponent(
    final ReportDesignRepository repository,
    final LocalityRepository localityRepository,
    final OrganizationRepository organizationRepository,
    final ReportScopeValidator reportScopeValidator,
    final PropertyModelRepository propertyModelRepository,
    @Value("${app.reportPath}") final String fileReportPath,
    @Value("${spring.data.neo4j.uri}") final String urlConnection,
    @Value("${spring.data.neo4j.username}") final String userName,
    @Value("${spring.data.neo4j.password}") final String password
  ) {
    this.repository = repository;
    this.localityRepository = localityRepository;
    this.organizationRepository = organizationRepository;
    this.reportScopeValidator = reportScopeValidator;
    this.propertyModelRepository = propertyModelRepository;
    this.fileReportPath = fileReportPath;
    this.urlConnection = urlConnection;
    this.userName = userName;
    this.password = password;
  }

  private void validateParameters(
    final Collection<? extends ReportParamsRequest> parameters
  ) {
    if (parameters == null || parameters.isEmpty()) return;
    for (final ReportParamsRequest parameter : parameters) {
      this.validateParameter(parameter);
    }
  }

  private void validateParameter(final ReportParamsRequest parameter) {
    final PropertyModel propertyModelParameter = this.getPropertyModel(parameter);
    switch (parameter.getType()) {
      case "Integer": {
        final IntegerModel integerModel = (IntegerModel) propertyModelParameter;
        if (integerModel.isRequired() && parameter.getValue() == null) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_NULL + "$" + propertyModelParameter.getLabel());
        }
        if (parameter.getValue() == null) {
          return;
        }
        final Integer value = Integer.valueOf(parameter.getValue());
        if (integerModel.getMin() != null
          && integerModel.getMin() > value) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_MIN + "$" + propertyModelParameter.getLabel());
        }
        if (integerModel.getMax() != null
          && integerModel.getMax() < value) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_MAX + "$" + propertyModelParameter.getLabel());
        }
        break;
      }
      case "Text": {
        final String value = parameter.getValue();
        final TextModel textModel = (TextModel) propertyModelParameter;
        if (textModel.isRequired()
          && (value == null || value.isEmpty())) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_EMPTY + "$" + propertyModelParameter.getLabel());
        }
        if (Objects.isNull(value)) {
          return;
        }
        if (textModel.getMin() != null
          && textModel.getMin() > value.length()) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_MIN + "$" + propertyModelParameter.getLabel());
        }
        if (textModel.getMax() != null
          && textModel.getMax() < value.length()) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_MAX + "$" + propertyModelParameter.getLabel());
        }
        break;
      }
      case "Date": {
        final DateModel dateModel = (DateModel) propertyModelParameter;
        if (dateModel.isRequired() && parameter.getValue() == null) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_NULL + "$" + propertyModelParameter.getLabel());
        }
        if (parameter.getValue() == null) {
          return;
        }
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        final LocalDateTime value = LocalDateTime.parse(parameter.getValue(), formatter);
        if (dateModel.getMin() != null && dateModel.getMin().isAfter(value)) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_MIN + "$" + propertyModelParameter.getLabel());
        }
        if (dateModel.getMax() != null && dateModel.getMax().isBefore(value)) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_MAX + "$" + propertyModelParameter.getLabel());
        }
        break;
      }
      case "Toggle": {
        final ToggleModel toggleModel = (ToggleModel) propertyModelParameter;
        if (toggleModel.isRequired() && parameter.getValue() == null) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_MAX + "$" + toggleModel.getLabel());
        }
        break;
      }
      case "UnitSelection": {
        final UnitSelectionModel unitSelectionModel = (UnitSelectionModel) propertyModelParameter;
        if (unitSelectionModel.isRequired() && parameter.getSelectedValue() == null) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_NULL + "$" + unitSelectionModel.getLabel());
        }
        break;
      }
      case "Selection": {
        final SelectionModel selectionModel = (SelectionModel) propertyModelParameter;
        if (selectionModel.isRequired() && parameter.getValue() == null) {
          throw new NegocioException(PROPERTY_VALUE_NOT_NULL + "$" + selectionModel.getLabel());
        }
        if (parameter.getValue() == null) {
          return;
        }
        final List<String> values = Arrays.stream(parameter.getValue().split(","))
          .collect(Collectors.toList());
        if (selectionModel.isRequired() && values.isEmpty()) {
          throw new NegocioException(PROPERTY_VALUE_NOT_NULL + "$" + selectionModel.getLabel());
        }
        break;
      }
      case "TextArea": {
        final TextAreaModel textAreaModel = (TextAreaModel) propertyModelParameter;
        if (textAreaModel.isRequired()
          && (parameter.getValue() == null || parameter.getValue().isEmpty())) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_EMPTY + "$" + propertyModelParameter.getLabel());
        }
        if (textAreaModel.getMin() != null
          && textAreaModel.getMin() > parameter.getValue().length()) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_MIN + "$" + propertyModelParameter.getLabel());
        }
        if (textAreaModel.getMax() != null
          && textAreaModel.getMax() < parameter.getValue().length()) {
          throw new NegocioException(
            PROPERTY_VALUE_NOT_MAX + "$" + propertyModelParameter.getLabel());
        }
        break;
      }
      case "Num": {
        final NumberModel decimalModel = (NumberModel) propertyModelParameter;
        if (decimalModel.isRequired() && parameter.getValue() == null) {
          throw new NegocioException(PROPERTY_VALUE_NOT_NULL + "$" + propertyModelParameter.getLabel());
        }
        if (Objects.isNull(parameter.getValue())) {
          return;
        }
        final Double value = Double.valueOf(parameter.getValue());
        if (decimalModel.getMin() != null
          && decimalModel.getMin() > value) {
          throw new NegocioException(PROPERTY_VALUE_NOT_MIN + "$" + propertyModelParameter.getLabel());
        }
        if (decimalModel.getMax() != null
          && decimalModel.getMax() < value) {
          throw new NegocioException(PROPERTY_VALUE_NOT_MAX + "$" + propertyModelParameter.getLabel());
        }
        break;
      }
      case "Currency": {
        final CurrencyModel currencyModel = (CurrencyModel) propertyModelParameter;
        if (currencyModel.isRequired() && parameter.getValue() == null) {
          throw new NegocioException(PROPERTY_VALUE_NOT_NULL + "$" + propertyModelParameter.getLabel());
        }
        break;
      }
      case "LocalitySelection": {
        final LocalitySelectionModel localitySelection = (LocalitySelectionModel) propertyModelParameter;
        if (localitySelection.isRequired() && (parameter.getSelectedValues() == null
          || parameter.getSelectedValues().isEmpty())) {
          throw new NegocioException(PROPERTY_VALUE_NOT_NULL + "$" + propertyModelParameter.getLabel());
        }
        break;
      }
      case "OrganizationSelection": {
        final OrganizationSelectionModel organizationSelection = (OrganizationSelectionModel) propertyModelParameter;
        if (organizationSelection.isRequired() &&
          (parameter.getSelectedValues() == null || parameter.getSelectedValues().isEmpty())
        ) {
          throw new NegocioException(PROPERTY_VALUE_NOT_NULL + "$" + propertyModelParameter.getLabel());
        }
        break;
      }
      default: {
        throw new NegocioException(PROPERTY_MODEL_INVALID_TYPE);
      }
    }
  }

  private PropertyModel getPropertyModel(final ReportParamsRequest property) {
    return this.propertyModelRepository.findById(property.getIdPropertyModel())
      .orElseThrow(() -> new NegocioException(PROPERTY_REQUIRED_NOT_FOUND + "$" + property.getIdPropertyModel()));
  }

  public GeneratedReport execute(final ReportRequest request, final String authorization) {

    this.validateScope(request, authorization);
    this.validateParameters(request.getParams());

    final JasperUtils jasperUtils = new JasperUtils();

    final ReportDesign reportDesign = this.findByIdWithRelationships(request.getIdReportModel());

    final File compiledFileMain = reportDesign.getCompiledSource().stream()
      .filter(File::getMain)
      .findFirst()
      .orElseThrow(() -> new NegocioException(ApplicationMessage.REPORT_DESIGN_MAIN_FILE_NOT_FOUND));

    final Path path = this.getPath(compiledFileMain.getUniqueNameKey());

    try (final Connection connection = DriverManager.getConnection(
      "jdbc:neo4j:" + this.urlConnection + "/neo4j",
      this.userName,
      this.password
    )) {
      final JasperReport jasperReportMain = JasperUtils.getJasperReportFromJasperFile(path.toFile());

      final Map<String, Object> params = this.getParams(request);

      this.subReportsAsParams(reportDesign, params);

      final byte[] reportBytes = jasperUtils.print(params, connection, request.getFormat(), jasperReportMain);

      final GeneratedReport generatedReport = new GeneratedReport();

      generatedReport.setResource(this.convertToByteArrayResource(reportBytes));

      this.defineFileNameAndMediaType(generatedReport, request);

      return generatedReport;
    } catch (final ClassNotFoundException | IOException | SQLException e) {
      e.printStackTrace();
      throw new NegocioException(ApplicationMessage.REPORT_GENERATE_ERROR);
    }
  }

  private void validateScope(final ReportRequest request, final String authorization) {
    this.reportScopeValidator.execute(new ArrayList<>(request.getScope()), request.getIdPlan(), authorization);
  }

  private ReportDesign findByIdWithRelationships(final Long idReportDesign) {
    return this.repository.findByIdWithRelationships(idReportDesign)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.REPORT_DESIGN_NOT_FOUND));
  }

  private void subReportsAsParams(final ReportDesign reportDesign, final Map<String, Object> params) {

    final List<File> compiledSubReports = reportDesign.getCompiledSource().stream()
      .filter(f -> !f.getMain())
      .collect(Collectors.toList());

    for (final File compiledSubReport : compiledSubReports) {
      try {
        final Path path = this.getPath(compiledSubReport.getUniqueNameKey());
        final JasperReport jasperSubReport = JasperUtils.getJasperReportFromJasperFile(path.toFile());

        final String userGivenName = compiledSubReport.getUserGivenName().substring(
          0,
          compiledSubReport.getUserGivenName().lastIndexOf(".")
        );

        params.put(userGivenName, jasperSubReport);
      } catch (final ClassNotFoundException | IOException e) {
        e.printStackTrace();
        throw new NegocioException(ApplicationMessage.REPORT_GENERATE_ERROR);
      }
    }

  }

  private Map<String, Object> getParams(final ReportRequest request) {
    final Map<String, Object> parametros = new HashMap<>();
    final List<ReportParamsRequest> params = request.getParams();
    if (params != null) {
      params.forEach(param -> parametros.put(
        this.getPropertyModelName(param.getIdPropertyModel()),
        this.getValue(param)
      ));
    }
    final String scope = request.getScope().stream()
      .map(Object::toString)
      .collect(Collectors.joining(","));
    parametros.put("scope", scope);
    return parametros;
  }

  private String getPropertyModelName(final Long idPropertyModel) {
    final PropertyModel propertyModel = this.propertyModelRepository.findById(idPropertyModel)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PROPERTY_MODEL_NOT_FOUND));
    return propertyModel.getName();
  }

  private Object getValue(final ReportParamsRequest param) {
    switch (param.getType()) {
      case "Double":
      case "Currency":
        if (Objects.isNull(param.getValue())) return null;
        return Double.valueOf(param.getValue());
      case "Integer":
        if (Objects.isNull(param.getValue())) return null;
        return Integer.valueOf(param.getValue());
      case "Date":
        if (Objects.isNull(param.getValue())) return null;
        return "'" + param.getValue() + "'";
      case "Toggle":
        if (Objects.isNull(param.getValue())) return null;
        return BooleanUtils.toBoolean(param.getValue());
      case "LocalitySelection": {
        if (Objects.isNull(param.getSelectedValues())) return null;
        final Iterable<Locality> localities = this.localityRepository.findAllById(param.getSelectedValues(), 0);
        final StringJoiner joiner = new StringJoiner(",");
        localities.forEach(locality -> joiner.add(locality.getId().toString()));
        return joiner.toString();
      }
      case "OrganizationSelection": {
        if (Objects.isNull(param.getSelectedValues())) return null;
        final Iterable<Organization> organizations = this.organizationRepository.findAllById(param.getSelectedValues());
        final StringJoiner joiner = new StringJoiner(",");
        organizations.forEach(organization -> joiner.add(organization.getId().toString()));
        return joiner.toString();
      }
      case "UnitSelection":
        if (Objects.isNull(param.getSelectedValue())) return null;
        return param.getSelectedValue();
      case "Selection": {
        if (StringUtils.isEmpty(param.getValue())) return null;
        final String[] split = param.getValue().split(",");
        final StringJoiner joiner = new StringJoiner("','", "'", "'");
        Arrays.stream(split).forEach(joiner::add);
        return joiner.toString();
      }
      default:
        if (Objects.isNull(param.getValue())) return null;
        return param.getValue();
    }
  }

  private ByteArrayResource convertToByteArrayResource(final byte[] report) {
    return new ByteArrayResource(report);
  }

  private void defineFileNameAndMediaType(final GeneratedReport generatedReport, final ReportRequest request) {
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
