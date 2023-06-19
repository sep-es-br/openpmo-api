package br.gov.es.openpmo.utils;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.reports.ReportFormat;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;

import java.io.*;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.*;

public class JasperUtils {

  public static OutputStream compileJrxml(final File file) throws IOException, JRException {
    final OutputStream outputStream = new ByteArrayOutputStream();
    final InputStream inputStream = Files.newInputStream(file.toPath());
    JasperCompileManager.compileReportToStream(inputStream, outputStream);
    return outputStream;
  }

  public static JasperReport getJasperReportFromJasperFile(final File file) throws IOException, ClassNotFoundException {
    final InputStream inputStream = Files.newInputStream(file.toPath());
    try (final ObjectInputStream oin = new ObjectInputStream(inputStream)) {
      return (JasperReport) oin.readObject();
    }
  }


  public byte[] print(
    final Map<String, Object> params,
    final Connection connection,
    final ReportFormat tipo,
    final JasperReport jasperReport
  ) {

    final byte[] report;

    try {

      final ByteArrayOutputStream out = new ByteArrayOutputStream();

      final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);

      switch (tipo) {
        case PDF:
          report = JasperExportManager.exportReportToPdf(jasperPrint);
          break;
        case HTML:
          final HtmlExporter exporterHtml = new HtmlExporter();
          exporterHtml.setExporterInput(new SimpleExporterInput(jasperPrint));
          final Map<String, String> images = new HashMap<>();
          final SimpleHtmlExporterOutput simpleHtmlExporterOutput = new SimpleHtmlExporterOutput(out);
          simpleHtmlExporterOutput.setImageHandler(new HtmlResourceHandler() {

            @Override
            public String getResourcePath(final String id) {
              return images.get(id);
            }

            @Override
            public void handleResource(final String id, final byte[] data) {
              if (id.endsWith("JPEG") || id.endsWith("jpeg"))
                images.put(id, "data:image/jpeg;base64," + Arrays.toString(Base64.getEncoder().encode(data)));
              if (id.endsWith("JPG") || id.endsWith("jpg"))
                images.put(id, "data:image/jpg;base64," + Arrays.toString(Base64.getEncoder().encode(data)));
              if (id.endsWith("PNG") || id.endsWith("png"))
                images.put(id, "data:image/jpg;base64," + Arrays.toString(Base64.getEncoder().encode(data)));
              if (id.endsWith("SVG") || id.endsWith("svg"))
                images.put(id, "data:image/svg+xml;base64," + Arrays.toString(Base64.getEncoder().encode(data)));
            }
          });
          exporterHtml.setExporterOutput(simpleHtmlExporterOutput);
          exporterHtml.exportReport();
          report = out.toByteArray();
          break;
        case ODT:
          final JRDocxExporter exporter = new JRDocxExporter();
          exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
          exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
          exporter.exportReport();
          report = out.toByteArray();
          break;
        case XLS:
          final JRXlsxExporter exporterXlsx = new JRXlsxExporter();
          exporterXlsx.setExporterInput(new SimpleExporterInput(jasperPrint));

          final SimpleXlsxReportConfiguration xlsReportConfiguration = new SimpleXlsxReportConfiguration();
          xlsReportConfiguration.setOnePagePerSheet(false);
          xlsReportConfiguration.setRemoveEmptySpaceBetweenRows(true);
          xlsReportConfiguration.setDetectCellType(false);
          xlsReportConfiguration.setWhitePageBackground(false);
          exporterXlsx.setConfiguration(xlsReportConfiguration);

          final SimpleOutputStreamExporterOutput simpleOutputStreamExporterOutput = new SimpleOutputStreamExporterOutput(out);
          exporterXlsx.setExporterOutput(simpleOutputStreamExporterOutput);
          exporterXlsx.exportReport();
          report = out.toByteArray();
          break;
        case CSV:
          final JRCsvExporter exporterCSV = new JRCsvExporter();
          exporterCSV.setExporterInput(new SimpleExporterInput(jasperPrint));

          final SimpleHtmlExporterOutput simpleHtmlExporterOutputToCSV = new SimpleHtmlExporterOutput(out);
          exporterCSV.setExporterOutput(simpleHtmlExporterOutputToCSV);
          exporterCSV.exportReport();
          report = out.toByteArray();
          break;
        case TXT:
          final JRTextExporter exporterTxt = new JRTextExporter();
          exporterTxt.setExporterInput(new SimpleExporterInput(jasperPrint));
          exporterTxt.setExporterOutput(new SimpleWriterExporterOutput(out));
          // https://jasperreports.sourceforge.net/sample.reference/text/index.html
          final SimpleTextReportConfiguration configuration = new SimpleTextReportConfiguration();
          configuration.setCharHeight(13.948f);
          configuration.setCharWidth(7.238f);
          exporterTxt.setConfiguration(configuration);
          exporterTxt.exportReport();
          report = out.toByteArray();
          break;
        case RTF:
          final JRRtfExporter exportRtf = new JRRtfExporter();
          exportRtf.setExporterInput(new SimpleExporterInput(jasperPrint));
          exportRtf.setExporterOutput(new SimpleWriterExporterOutput(out));
          exportRtf.exportReport();
          report = out.toByteArray();
          break;
        case XML:
          final JRXmlExporter exportXml = new JRXmlExporter();
          exportXml.setExporterInput(new SimpleExporterInput(jasperPrint));
          exportXml.setExporterOutput(new SimpleXmlExporterOutput(out));
          exportXml.exportReport();
          report = out.toByteArray();
          break;
        default:
          throw new NegocioException(ApplicationMessage.REPORT_GENERATE_UNKNOWN_TYPE_ERROR);
      }

    } catch (final JRException e) {
      e.printStackTrace();
      throw new NegocioException(ApplicationMessage.REPORT_GENERATE_ERROR);
    }

    return report;
  }

  private JRDataSource getDataSource(final List<?> lista) {
    if (lista == null || lista.isEmpty())
      return new JREmptyDataSource();
    else {
      return new JRBeanCollectionDataSource(lista);
    }
  }

}
