package br.gov.es.openpmo.apis.edocs.request;

import java.util.Collections;
import java.util.List;

public class FetchProcessBody {

  private final List<String> protocolos;

  public FetchProcessBody(final String protocol) {
    this.protocolos = Collections.singletonList(protocol);
  }

  public List<String> getProtocolos() {
    return this.protocolos;
  }

}
