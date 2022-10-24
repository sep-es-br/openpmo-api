package br.gov.es.openpmo.repository.custom;

import org.neo4j.driver.Session;

//mport org.neo4j.ogm.session.Session;


public interface CustomRepository {

  Session getSession();

}
