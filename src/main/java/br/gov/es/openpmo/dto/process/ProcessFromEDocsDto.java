package br.gov.es.openpmo.dto.process;

import br.gov.es.openpmo.apis.edocs.ProcessTimeline;
import br.gov.es.openpmo.apis.edocs.response.ProcessResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProcessFromEDocsDto {
    private final String processNumber;
    private final String status;
    private final String subject;
    private final String currentOrganization;
    private final long lengthOfStayOn;
    private final boolean priority;
    private final List<ProcessTimelineDto> history;

    public ProcessFromEDocsDto(
            final String processNumber,
            final String status,
            final String subject,
            final String currentOrganization,
            final long lengthOfStayOn,
            final boolean priority,
            final List<ProcessTimelineDto> history
    ) {
        this.processNumber = processNumber;
        this.status = status;
        this.subject = subject;
        this.currentOrganization = currentOrganization;
        this.lengthOfStayOn = lengthOfStayOn;
        this.priority = priority;
        this.history = Collections.unmodifiableList(history);
        history.sort(Comparator.comparing(ProcessTimelineDto::getUpdateDate).reversed());
    }

    public static ProcessFromEDocsDto of(final ProcessResponse process) {
        List<ProcessTimeline> timeline = process.timeline().stream()
                .sorted(Comparator.comparing(ProcessFromEDocsDto::getDate).reversed())
                .collect(Collectors.toList());

        String currentOrganization = process.getCurrentOrganizationAbbreviation();

        LocalDateTime date = LocalDateTime.now();
        for (ProcessTimeline processTimeline : timeline) {
            if (!Objects.equals(processTimeline.detail().getAbbreviation(), currentOrganization)) {
                break;
            }
            date = processTimeline.detail().getDate();
        }
        long lengthOfStayOn = date.until(LocalDateTime.now(), ChronoUnit.DAYS);

        return new ProcessFromEDocsDto(
                process.getProcessNumber(),
                process.getStatus(),
                process.getSubject(),
                currentOrganization,
                lengthOfStayOn,
                process.getPriority(),
                ProcessTimelineDto.of(timeline)
        );
    }

    @JsonIgnore
    private static LocalDateTime getDate(ProcessTimeline processTimeline) {
        return processTimeline.detail().getDate();
    }

    public List<ProcessTimelineDto> getHistory() {
        return this.history;
    }

    public String getProcessNumber() {
        return this.processNumber;
    }

    public String getStatus() {
        return this.status;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getCurrentOrganization() {
        return this.currentOrganization;
    }

    public long getLengthOfStayOn() {
        return this.lengthOfStayOn;
    }

    public boolean getPriority() {
        return this.priority;
    }
}
