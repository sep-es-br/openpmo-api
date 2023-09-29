package br.gov.es.openpmo.dto.dashboards;

import java.util.List;

public class DashboardMenuResponse {

  private Long id;
  private Long idWorkpackModel;
  private String name;
  private String icon;
  private Boolean linked;
  private List<DashboardMenuResponse> workpacks;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getIdWorkpackModel() {
    return idWorkpackModel;
  }

  public void setIdWorkpackModel(Long idWorkpackModel) {
    this.idWorkpackModel = idWorkpackModel;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public Boolean getLinked() {
    return linked;
  }

  public void setLinked(Boolean linked) {
    this.linked = linked;
  }

  public List<DashboardMenuResponse> getWorkpacks() {
    return workpacks;
  }

  public void setWorkpacks(List<DashboardMenuResponse> workpacks) {
    this.workpacks = workpacks;
  }
}
