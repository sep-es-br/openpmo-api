package br.gov.es.openpmo.dto.plan;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.gov.es.openpmo.utils.ApplicationMessage;

public class PlanUpdateDto {

    @NotNull(message = ApplicationMessage.ID_NOT_NULL)
    private Long id;
    @NotBlank(message = ApplicationMessage.NAME_NOT_BLANK)
    private String name;
    @NotBlank(message = ApplicationMessage.FULLNAME_NOT_BLANK)
    private String fullName;
    @NotNull(message = ApplicationMessage.START_NOT_NULL)
    private LocalDate start;
    @NotNull(message = ApplicationMessage.FINISH_NOT_NULL)
    private LocalDate finish;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getFinish() {
        return finish;
    }

    public void setFinish(LocalDate finish) {
        this.finish = finish;
    }
}
