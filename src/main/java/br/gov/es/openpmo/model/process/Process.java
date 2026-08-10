package br.gov.es.openpmo.model.process;

import br.gov.es.openpmo.dto.process.ProcessCreateDto;
import br.gov.es.openpmo.dto.process.ProcessFromEDocsDto;
import br.gov.es.openpmo.dto.process.ProcessReadonlyDetailDto;
import br.gov.es.openpmo.dto.process.ProcessUpdateDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.utils.ObjectUtils;

import java.time.LocalDateTime;

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

  private String actingOrganization;
  private String actingSector;
  private LocalDateTime actingDate;
  private LocalDateTime lastDispatchDate;
  private Long lengthOfStayOnSector;

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

    final String actingOrganization,
    final String actingSector,
    final LocalDateTime actingDate,
    final LocalDateTime lastDispatchDate,
    final Long lengthOfStayOnSector,

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

    this.actingOrganization = actingOrganization;
    this.actingSector = actingSector;
    this.actingDate = actingDate;
    this.lastDispatchDate = lastDispatchDate;
    this.lengthOfStayOnSector = lengthOfStayOnSector;

    this.workpack = workpack;
  }

  public static Process of(
    final ProcessCreateDto request,
    final Workpack workpack
  ) {
    final ProcessReadonlyDetailDto d = request.getReadonlyDetail();
  
    return new Process(
      request.getName(),
      d.getSubject(),
      d.getCurrentOrganization(),
      d.getProcessNumber(),
      d.getLengthOfStayOn(),
      request.getNote(),
      d.getPriority(),
      d.getStatus(),
  
      d.getActingOrganization(),
      d.getActingSector(),
      d.getActingDate(),
      d.getLastDispatchDate(),
      d.getLengthOfStayOnSector(),
  
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

  public String getActingOrganization() {
    return actingOrganization;
  }

  public void setActingOrganization(String actingOrganization) {
    this.actingOrganization = actingOrganization;
  }

  public String getActingSector() {
    return actingSector;
  }

  public void setActingSector(String actingSector) {
    this.actingSector = actingSector;
  }

  public LocalDateTime getActingDate() {
    return actingDate;
  }

  public void setActingDate(LocalDateTime actingDate) {
    this.actingDate = actingDate;
  }

  public LocalDateTime getLastDispatchDate() {
    return lastDispatchDate;
  }

  public void setLastDispatchDate(LocalDateTime lastDispatchDate) {
    this.lastDispatchDate = lastDispatchDate;
  }

  public Long getLengthOfStayOnSector() {
    return lengthOfStayOnSector;
  }

  public void setLengthOfStayOnSector(Long lengthOfStayOnSector) {
    this.lengthOfStayOnSector = lengthOfStayOnSector;
  }

  public void update(
    final ProcessUpdateDto request,
    final ProcessFromEDocsDto fromEDocs
  ) {
    ObjectUtils.updateIfPresent(request::getName, this::setName);
    ObjectUtils.updateIfPresent(request::getNote, this::setNote);

    this.updateUsingEDocsData(fromEDocs);
  }

  public void updateUsingEDocsData(final ProcessFromEDocsDto dto) {
    ObjectUtils.updateIfPresent(dto::getSubject, this::setSubject);
    ObjectUtils.updateIfPresent(dto::getCurrentOrganization, this::setCurrentOrganization);
    ObjectUtils.updateIfPresent(dto::getLengthOfStayOn, this::setLengthOfStayOn);
    ObjectUtils.updateIfPresent(dto::getPriority, this::setPriority);
    ObjectUtils.updateIfPresent(dto::getStatus, this::setStatus);
  
    ObjectUtils.updateIfPresent(dto::getActingOrganization, this::setActingOrganization);
    ObjectUtils.updateIfPresent(dto::getActingSector, this::setActingSector);
    ObjectUtils.updateIfPresent(dto::getActingDate, this::setActingDate);
    ObjectUtils.updateIfPresent(dto::getLastDispatchDate, this::setLastDispatchDate);
    ObjectUtils.updateIfPresent(dto::getLengthOfStayOnSector, this::setLengthOfStayOnSector);
  }

}
