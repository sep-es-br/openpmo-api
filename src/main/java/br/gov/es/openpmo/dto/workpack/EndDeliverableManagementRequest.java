package br.gov.es.openpmo.dto.workpack;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class EndDeliverableManagementRequest {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate endManagementDate;

    private final String reason;

    @JsonCreator
    public EndDeliverableManagementRequest(final LocalDate endManagementDate, String reason) {
        this.endManagementDate = endManagementDate;
        this.reason = reason;
    }

    public LocalDate getEndManagementDate() {
        return this.endManagementDate;
    }

    public String getReason() {
        return reason;
    }

}
