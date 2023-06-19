package br.gov.es.openpmo.service.reports.models;

public interface TemplateSourceRequest {

  String getMimeType();

  String getUniqueNameKey();

  String getUserGivenName();

  Boolean getMain();

}
