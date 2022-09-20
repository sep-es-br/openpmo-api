package br.gov.es.openpmo.configuration;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class ModelMapperConfiguration {

  DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Bean
  public ModelMapper modelMapper() {

    final ModelMapper modelMapper = new ModelMapper();
    modelMapper.addConverter(this.getStringToLocalDateConverter());
    modelMapper.addConverter(this.getLocalDateToStringConverter());
    modelMapper.addConverter(this.getStringToLocalDateTimeConverter());
    modelMapper.addConverter(this.getLocalDateTimeToStringConverter());
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    return modelMapper;
  }

  private AbstractConverter<String, LocalDate> getStringToLocalDateConverter() {
    return new AbstractConverter<String, LocalDate>() {

      @Override
      protected LocalDate convert(final String source) {
        if(source == null) {
          return null;
        }

        return LocalDate.parse(source, ModelMapperConfiguration.this.localDateFormatter);
      }
    };
  }

  private AbstractConverter<LocalDate, String> getLocalDateToStringConverter() {
    return new AbstractConverter<LocalDate, String>() {

      @Override
      protected String convert(final LocalDate source) {
        if(source == null) {
          return null;
        }

        return ModelMapperConfiguration.this.localDateFormatter.format(source);
      }
    };
  }

  private AbstractConverter<String, LocalDateTime> getStringToLocalDateTimeConverter() {
    return new AbstractConverter<String, LocalDateTime>() {

      @Override
      protected LocalDateTime convert(final String source) {
        if(source == null) {
          return null;
        }

        return LocalDateTime.parse(source, ModelMapperConfiguration.this.localDateTimeFormatter);
      }
    };
  }

  private AbstractConverter<LocalDateTime, String> getLocalDateTimeToStringConverter() {
    return new AbstractConverter<LocalDateTime, String>() {

      @Override
      protected String convert(final LocalDateTime source) {
        if(source == null) {
          return null;
        }

        return ModelMapperConfiguration.this.localDateFormatter.format(source);
      }
    };
  }

}
