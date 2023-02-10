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
@RequestMapping("/versions")
public class VersionController {

  @Value("${app.version}")
  private String version;

  @GetMapping
  public ResponseEntity<ResponseBase<VersionDto>> currentVersion() {
    final ResponseBase<VersionDto> response = new ResponseBase<VersionDto>()
        .setSuccess(true)
        .setData(new VersionDto(this.version));

    return ResponseEntity.ok(response);
  }

  private static class VersionDto {

    private final String version;

    public VersionDto(final String version) {
      this.version = version;
    }

    public String getVersion() {
      return this.version;
    }

  }

}
