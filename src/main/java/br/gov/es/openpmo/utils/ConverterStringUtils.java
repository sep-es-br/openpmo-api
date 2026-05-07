package br.gov.es.openpmo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ConverterStringUtils {

  public Map<String, String> convertQueryStringToHashMap(String source) {
        Map<String, String> data = new HashMap<>();

        if (source == null || source.isEmpty()) {
            return data;
        }

        String[] arrParameters = source.split("&");

        for (String param : arrParameters) {

            String[] pair = param.split("=", 2); // 👈 LIMITADOR IMPORTANTE

            try {
                String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8.name());

                String value = pair.length > 1
                    ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name())
                    : "";
                
                data.put(key, value);
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }

        return data;
    }

}
