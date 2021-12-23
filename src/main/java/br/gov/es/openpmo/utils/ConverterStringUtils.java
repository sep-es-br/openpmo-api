package br.gov.es.openpmo.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConverterStringUtils {

  public Map<String, String> convertQueryStringToHashMap(final String source) {
    final Map<String, String> data = new HashMap<>();
    final String[] arrParameters = source.split("&");

    for(final String tempParameterString : arrParameters) {
      final String[] arrTempParameter = tempParameterString.split("=");
      final String parameterKey = arrTempParameter[0];

      if(arrTempParameter.length >= 2) {
        final String parameterValue = arrTempParameter[1];
        data.put(parameterKey, parameterValue);
      }
      else {
        data.put(parameterKey, "");
      }
    }

    return data;
  }

}
