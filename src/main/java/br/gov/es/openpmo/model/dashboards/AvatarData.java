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

    public static AvatarData of(DatasheetAvatar from) {
        if (from == null) {
            return null;
        }

        final AvatarData to = new AvatarData();

        to.setIdAvatar(from.getId());
        to.setUrl(from.getUrl());
        to.setName(from.getName());
        to.setMimeType(from.getMimeType());

        return to;
    }

    public void setIdAvatar(Long idAvatar) {
        this.idAvatar = idAvatar;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getIdAvatar() {
        return idAvatar;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getMimeType() {
        return mimeType;
    }

}
