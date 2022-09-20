package br.gov.es.openpmo.model.process;

import br.gov.es.openpmo.apis.edocs.response.ProcessResponse;
import br.gov.es.openpmo.dto.process.ProcessCreateDto;
import br.gov.es.openpmo.dto.process.ProcessUpdateDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.utils.ObjectUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Process extends Entity {

  private String name;
  private String subject;
  private String currentOrganization;
  private String processNumber;
  private Long lengthOfStayOn;
  private String note;
  private Boolean priority;
  private String status;

  @Relationship("IS_BELONGS_TO")
  private Workpack workpack;

  public Process() {
  }

  public Process(
    final String name,
    final String subject,
    final String currentOrganization,
    final String processNumber,
    final Long lengthOfStayOn,
    final String note,
    final Boolean priority,
    final String status,
    final Workpack workpack
  ) {
    this.name = name;
    this.subject = subject;
    this.currentOrganization = currentOrganization;
    this.processNumber = processNumber;
    this.lengthOfStayOn = lengthOfStayOn;
    this.note = note;
    this.priority = priority;
    this.status = status;
    this.workpack = workpack;
  }

  public static Process of(
    final ProcessCreateDto request,
    final Workpack workpack
  ) {
    return new Process(
      request.getName(),
      request.getReadonlyDetail().getSubject(),
      request.getReadonlyDetail().getCurrentOrganization(),
      request.getReadonlyDetail().getProcessNumber(),
      request.getReadonlyDetail().getLengthOfStayOn(),
      request.getNote(),
      request.getReadonlyDetail().getPriority(),
      request.getReadonlyDetail().getStatus(),
      workpack
    );
  }

  public Long getIdWorkpack() {
    return this.workpack.getId();
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getSubject() {
    return this.subject;
  }

  public void setSubject(final String subject) {
    this.subject = subject;
  }

  public String getCurrentOrganization() {
    return this.currentOrganization;
  }

  public void setCurrentOrganization(final String currentOrganization) {
    this.currentOrganization = currentOrganization;
  }

  public String getProcessNumber() {
    return this.processNumber;
  }

  public void setProcessNumber(final String processNumber) {
    this.processNumber = processNumber;
  }

  public Long getLengthOfStayOn() {
    return this.lengthOfStayOn;
  }

  public void setLengthOfStayOn(final Long lengthOfStayOn) {
    this.lengthOfStayOn = lengthOfStayOn;
  }

  public String getNote() {
    return this.note;
  }

  public void setNote(final String note) {
    this.note = note;
  }

  public Boolean getPriority() {
    return this.priority;
  }

  public void setPriority(final Boolean priority) {
    this.priority = priority;
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public void update(
    final ProcessUpdateDto request,
    final ProcessResponse processByProtocol
  ) {
    ObjectUtils.updateIfPresent(request::getName, this::setName);
    ObjectUtils.updateIfPresent(request::getNote, this::setNote);
    this.updateUsingEDocsData(processByProtocol);
  }

  public void updateUsingEDocsData(final ProcessResponse process) {
    ObjectUtils.updateIfPresent(process::getSubject, this::setSubject);
    ObjectUtils.updateIfPresent(process::getCurrentOrganizationAbbreviation, this::setCurrentOrganization);
    ObjectUtils.updateIfPresent(process::lengthOfStayOn, this::setLengthOfStayOn);
    ObjectUtils.updateIfPresent(process::getPriority, this::setPriority);
    ObjectUtils.updateIfPresent(process::getStatus, this::setStatus);
  }

}
