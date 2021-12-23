package br.gov.es.openpmo.repository.custom;

import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomRepositoryImpl implements CustomRepository {

  @Autowired
  private Session session;

  @Override
  public Session getSession() {
    return this.session;
  }

}
