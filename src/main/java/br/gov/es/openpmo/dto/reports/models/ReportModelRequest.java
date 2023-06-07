package br.gov.es.openpmo.dto.reports.models;

import javax.validation.constraints.NotNull;

public class ReportModelRequest {

	@NotNull
	private Long idReportModel;
	
	public Long getIdReportModel() {
		return idReportModel;
	}
	
	public void setIdReportModel(Long idReportModel) {
		this.idReportModel = idReportModel;
	}

}
