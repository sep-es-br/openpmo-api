package br.gov.es.openpmo.apis.edocs.response;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ProcessHistoryResponse {

  private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private final LocalDateTime date;
  private final String name;
  private final String abbreviation;

  public ProcessHistoryResponse(final LocalDateTime date, final String name, final String abbreviation) {
    this.date = date;
    this.name = name;
    this.abbreviation = abbreviation;
  }

  public ProcessHistoryResponse(final JSONObject json) {
    final JSONObject destino = json.optJSONObject("destino");
    final JSONObject setor = json.getJSONObject("papel").optJSONObject("setor");

    this.date = LocalDateTime.parse(json.optString("dataHora"), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    this.name = Optional.ofNullable(destino)
      .map(jsonObject -> jsonObject.getString("nome"))
      .orElse(null);
    this.abbreviation = setor.optJSONObject("organizacao").getString("sigla");
  }

  public LocalDateTime getDate() {
    return this.date;
  }

  public String getName() {
    return this.name;
  }

  public String getAbbreviation() {
    return this.abbreviation;
  }
}
