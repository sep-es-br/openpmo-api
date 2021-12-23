package br.gov.es.openpmo.apis.acessocidadao.response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OperationalOrganizationResponse {

  private final String description;

  private final String abbreviation;

  private final String guid;

  private final List<OperationalOrganizationResponse> children;

  public OperationalOrganizationResponse(final JSONObject json) {
    this.description = json.getString("Descricao");
    this.abbreviation = json.getString("Sigla");
    this.guid = json.getString("Guid");
    this.children = new ArrayList<>();

    json.getJSONArray("Filhos").forEach(element -> {
      if(element instanceof JSONObject) {
        final JSONObject obj = (JSONObject) element;
        this.children.add(new OperationalOrganizationResponse(obj));
      }
    });
  }

  public String getDescription() {
    return this.description;
  }

  public String getAbbreviation() {
    return this.abbreviation;
  }

  public String getGuid() {
    return this.guid;
  }

  public List<OperationalOrganizationResponse> getChildren() {
    return this.children;
  }

}
