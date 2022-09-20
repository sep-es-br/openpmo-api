package br.gov.es.openpmo.apis.acessocidadao.response;

import org.json.JSONObject;

public class PublicAgentEmailResponse {

  private final String email;
  private final String corporateEmail;

  public PublicAgentEmailResponse(final JSONObject jsonObject) {
    this.email = jsonObject.optString("email");
    this.corporateEmail = jsonObject.optString("corporativo");
  }

  public String getEmail() {
    return this.email;
  }

  public String getCorporateEmail() {
    return this.corporateEmail;
  }

}
