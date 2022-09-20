package br.gov.es.openpmo.model.actors;

import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class AuthService extends Entity {

  private String server;
  private String endPoint;

  public String getServer() {
    return this.server;
  }

  public void setServer(final String server) {
    this.server = server;
  }

  public String getEndPoint() {
    return this.endPoint;
  }

  public void setEndPoint(final String endPoint) {
    this.endPoint = endPoint;
  }

}
