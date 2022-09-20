package br.gov.es.openpmo.dto.file;


import br.gov.es.openpmo.model.actors.File;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

public class AvatarDto {

  private final Long id;
  private final String url;
  private final String name;
  private final String mimeType;

  public AvatarDto(
    final File file,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    Objects.requireNonNull(uriComponentsBuilder);
    final String url = uriComponentsBuilder.cloneBuilder()
      .path("/avatar/" + file.getId() + "/person")
      .build().toUri().toString();

    this.id = file.getId();
    this.url = url;
    this.name = file.getUserGivenName();
    this.mimeType = file.getMimeType();
  }

  public AvatarDto() {
    this.id = null;
    this.url = null;
    this.name = null;
    this.mimeType = null;
  }

  public Long getId() {
    return this.id;
  }

  public String getUrl() {
    return this.url;
  }

  public String getName() {
    return this.name;
  }

  public String getMimeType() {
    return this.mimeType;
  }

}
