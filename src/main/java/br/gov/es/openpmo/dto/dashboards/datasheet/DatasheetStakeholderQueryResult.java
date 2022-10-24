package br.gov.es.openpmo.dto.dashboards.datasheet;

import br.gov.es.openpmo.model.actors.File;
import org.springframework.data.neo4j.repository.query.QueryResult;
import org.springframework.lang.Nullable;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

@QueryResult
public class DatasheetStakeholderQueryResult {

  private Long id;

  private String name;

  private String fullName;

  private String role;

  private File file;

  private Boolean organization;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public String getRole() {
    return this.role;
  }

  public void setRole(final String role) {
    this.role = role;
  }

  public File getFile() {
    return this.file;
  }

  public void setFile(final File file) {
    this.file = file;
  }

  public Boolean getOrganization() {
    return this.organization;
  }

  public void setOrganization(final Boolean organization) {
    this.organization = organization;
  }

  public DatasheetStakeholderResponse mapToResponse(final UriComponentsBuilder uriComponentsBuilder) {
    final DatasheetActor actor = this.getActor(uriComponentsBuilder);
    return new DatasheetStakeholderResponse(actor, this.role);
  }

  public DatasheetActor getActor(final UriComponentsBuilder uriComponentsBuilder) {
    final DatasheetAvatar avatar = this.getAvatar(uriComponentsBuilder);
    return new DatasheetActor(this.id, this.name, this.fullName, avatar, this.organization);
  }

  @Nullable
  private DatasheetAvatar getAvatar(final UriComponentsBuilder uriComponentsBuilder) {
    if(Objects.isNull(this.file)) {
      return null;
    }

    return new DatasheetAvatar(
      this.getFileId(),
      this.getFileUrl(uriComponentsBuilder),
      this.getFileName(),
      this.getFileType()
    );
  }

  @Nullable
  private Long getFileId() {
    return Optional.ofNullable(this.file)
      .map(File::getId)
      .orElse(null);
  }

  @Nullable
  private String getFileUrl(final UriComponentsBuilder uriComponentsBuilder) {
    if(Objects.isNull(this.file)) {
      return null;
    }

    return Objects.requireNonNull(uriComponentsBuilder)
      .cloneBuilder()
      .path(MessageFormat.format("/avatar/{0,number,#}/person", this.getFileId()))
      .build()
      .toUri()
      .toString();
  }

  @Nullable
  private String getFileName() {
    return Optional.ofNullable(this.file)
      .map(File::getUserGivenName)
      .orElse(null);
  }

  @Nullable
  private String getFileType() {
    return Optional.ofNullable(this.file)
      .map(File::getMimeType)
      .orElse(null);
  }

}
