package br.gov.es.openpmo.apis.acessocidadao.response;


import org.json.JSONObject;

public class PublicAgentResponse {

  private final String sub;
  private final String name;
  private final String nickname;
  private final String email;


  public PublicAgentResponse(final String sub, final String name, final String nickname, final String email) {
    this.sub = sub;
    this.name = name;
    this.nickname = nickname;
    this.email = email;
  }

  public PublicAgentResponse(final JSONObject json) {
    this.sub = json.optString("Sub");
    this.name = json.optString("Nome");
    this.nickname = json.optString("Apelido");
    this.email = json.optString("Email");
  }

  public String getSub() {
    return this.sub;
  }

  public String getName() {
    return this.name;
  }

  public String getNickname() {
    return this.nickname;
  }

  public String getEmail() {
    return this.email;
  }


  @Override
  public String toString() {
    return "PublicAgents{" +
           "sub='" + this.sub + '\'' +
           ", name='" + this.name + '\'' +
           ", nickname='" + this.nickname + '\'' +
           ", email='" + this.email + '\'' +
           '}';
  }
}
