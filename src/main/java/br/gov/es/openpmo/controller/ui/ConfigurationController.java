package br.gov.es.openpmo.controller.ui;

import br.gov.es.openpmo.dto.ResponseBase;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/configuration")
public class ConfigurationController {

  @Value("${app.theme:es}")
  private String theme;

  @GetMapping
  public ResponseEntity<ResponseBase<ConfigurationDto>> currentConfiguration() {
    final ResponseBase<ConfigurationDto> response = new ResponseBase<ConfigurationDto>()
        .setSuccess(true)
        .setData(new ConfigurationDto(this.theme));

    return ResponseEntity.ok(response);
  }

  private static class ConfigurationDto {

    private final String theme;

    public ConfigurationDto(final String theme) {
      this.theme = theme;
    }

    public String getTheme() {
      return this.theme;
    }

  }

}
