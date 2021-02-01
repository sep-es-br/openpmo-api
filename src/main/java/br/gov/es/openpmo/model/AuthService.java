package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class AuthService extends Entity {

	private String server;
	private String endPoint;

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}
}
