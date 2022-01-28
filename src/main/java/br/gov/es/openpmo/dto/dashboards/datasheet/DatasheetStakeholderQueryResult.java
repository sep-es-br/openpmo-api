package br.gov.es.openpmo.dto.dashboards.datasheet;

import br.gov.es.openpmo.model.actors.File;
import org.springframework.data.neo4j.annotation.QueryResult;
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

  public DatasheetStakeholderResponse mapToResponse(final UriComponentsBuilder uriComponentsBuilder) {
    final DatasheetActor actor = this.getActor(uriComponentsBuilder);
    return new DatasheetStakeholderResponse(actor, this.role);
  }

  private DatasheetActor getActor(final UriComponentsBuilder uriComponentsBuilder) {
    final DatasheetAvatar avatar = this.getAvatar(uriComponentsBuilder);
    return new DatasheetActor(this.id, this.name, this.fullName, avatar);
  }

  @Nullable
  private DatasheetAvatar getAvatar(final UriComponentsBuilder uriComponentsBuilder) {
    if (Objects.isNull(this.file)) {
      return null;
    }

    final Long fileId = this.getFileId();
    final String fileUrl = this.getFileUrl(uriComponentsBuilder);
    final String fileName = this.getFileName();
    final String fileType = this.getFileType();

    return new DatasheetAvatar(fileId, fileUrl, fileName, fileType);
  }

  @Nullable
  private Long getFileId() {
    return Optional.ofNullable(this.file)
        .map(File::getId)
        .orElse(null);
  }

  @Nullable
  private String getFileUrl(final UriComponentsBuilder uriComponentsBuilder) {
    if (Objects.isNull(this.file)) {
      return null;
    }

    final String path = MessageFormat.format("/avatar/{0,number,#}/person", this.getFileId());

    return Objects.requireNonNull(uriComponentsBuilder)
        .cloneBuilder()
        .path(path)
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
