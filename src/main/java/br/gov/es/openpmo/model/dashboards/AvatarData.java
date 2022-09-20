package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetAvatar;
import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class AvatarData extends Entity {

  private Long idAvatar;

  private String url;

  private String name;

  private String mimeType;

  public static AvatarData of(final DatasheetAvatar from) {
    if(from == null) {
      return null;
    }

    final AvatarData to = new AvatarData();

    to.setIdAvatar(from.getId());
    to.setUrl(from.getUrl());
    to.setName(from.getName());
    to.setMimeType(from.getMimeType());

    return to;
  }

  public Long getIdAvatar() {
    return this.idAvatar;
  }

  public void setIdAvatar(final Long idAvatar) {
    this.idAvatar = idAvatar;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(final String url) {
    this.url = url;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getMimeType() {
    return this.mimeType;
  }

  public void setMimeType(final String mimeType) {
    this.mimeType = mimeType;
  }

}
