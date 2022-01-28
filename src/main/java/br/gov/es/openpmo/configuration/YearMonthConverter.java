package br.gov.es.openpmo.configuration;

import org.springframework.core.convert.converter.Converter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class YearMonthConverter implements Converter<String, YearMonth> {

  @Override
  public YearMonth convert(final String text) {
    return YearMonth.parse(text, DateTimeFormatter.ofPattern("MM/yyyy"));
  }

}
