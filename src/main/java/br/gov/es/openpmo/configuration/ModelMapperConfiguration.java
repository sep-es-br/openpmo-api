package br.gov.es.openpmo.configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(getStringToLocalDateConverter());
        modelMapper.addConverter(getLocalDateToStringConverter());
        modelMapper.addConverter(getStringToLocalDateTimeConverter());
        modelMapper.addConverter(getLocalDateTimeToStringConverter());
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    private AbstractConverter<String, LocalDate> getStringToLocalDateConverter() {
        return new AbstractConverter<String, LocalDate>() {

			@Override
			protected LocalDate convert(String source) {
				if (source == null) {
					return null;
				}

				return LocalDate.parse(source, localDateFormatter);
			}
		};
    }

    private AbstractConverter<LocalDate, String> getLocalDateToStringConverter() {
        return new AbstractConverter<LocalDate, String>() {

			@Override
			protected String convert(LocalDate source) {
				if (source == null) {
					return null;
				}

				return localDateFormatter.format(source);
			}
		};
    }

	private AbstractConverter<String, LocalDateTime> getStringToLocalDateTimeConverter() {
		return new AbstractConverter<String, LocalDateTime>() {

			@Override
			protected LocalDateTime convert(String source) {
				if (source == null) {
					return null;
				}

				return LocalDateTime.parse(source, localDateTimeFormatter);
			}
		};
	}

	private AbstractConverter<LocalDateTime, String> getLocalDateTimeToStringConverter() {
		return new AbstractConverter<LocalDateTime, String>() {

			@Override
			protected String convert(LocalDateTime source) {
				if (source == null) {
					return null;
				}

				return localDateFormatter.format(source);
			}
		};
	}
}
