package br.gov.es.openpmo.model.journals;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@NodeEntity
public class JournalEntry extends Entity {

  private LocalDateTime date;

  private JournalType type;

  private JournalAction action;

  private String nameItem;

  private String description;

  private String reason;

  private LocalDate newDate;

  private LocalDate previousDate;

  @Relationship("SCOPE_TO")
  private Workpack workpack;

  @Relationship("IS_RECORDED_BY")
  private Person person;

  @Relationship(value = "IS_EVIDENCE_OF", direction = Relationship.INCOMING)
  private Set<File> files;

  public JournalEntry() {
  }

  public JournalEntry(
    final JournalType type,
    final JournalAction action,
    final String nameItem,
    final String description,
    final String reason,
    final LocalDate newDate,
    final LocalDate previousDate,
    final Workpack workpack,
    final Person person
  ) {
    this.type = type;
    this.action = action;
    this.nameItem = nameItem;
    this.description = description;
    this.reason = reason;
    this.newDate = newDate;
    this.previousDate = previousDate;
    this.workpack = workpack;
    this.person = person;
    this.date = LocalDateTime.now();
  }

  @Transient
  public void addFiles(final Iterable<? extends File> files) {
    if (Objects.isNull(files)) {
      return;
    }
    files.forEach(this::addFile);
  }

  @Transient
  public void addFile(final File file) {
    if (Objects.isNull(this.files)) {
      this.files = new HashSet<>();
    }

    this.files.add(file);
  }

  public LocalDateTime getDate() {
    return this.date;
  }

  public void setDate(final LocalDateTime date) {
    this.date = date;
  }

  public String getNameItem() {
    return this.nameItem;
  }

  public void setNameItem(final String nameItem) {
    this.nameItem = nameItem;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  @Transient
  public Long getWorkpackId() {
    return Optional.ofNullable(this.workpack).map(Workpack::getId).orElse(null);
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  public Person getPerson() {
    return this.person;
  }

  @Transient
  public Long getPersonId() {
    return Optional.ofNullable(this.person).map(Person::getId).orElse(null);
  }

  @Transient
  public String getPersonName() {
    return Optional.ofNullable(this.person).map(Person::getName).orElse(null);
  }

  public void setPerson(final Person person) {
    this.person = person;
  }

  public Set<File> getFiles() {
    return Optional.ofNullable(this.files).orElse(Collections.emptySet());
  }

  public void setFiles(final Set<File> files) {
    this.files = files;
  }

  public JournalType getType() {
    return this.type;
  }

  public void setType(final JournalType type) {
    this.type = type;
  }

  public JournalAction getAction() {
    return this.action;
  }

  public void setAction(final JournalAction action) {
    this.action = action;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public LocalDate getNewDate() {
    return newDate;
  }

  public void setNewDate(LocalDate newDate) {
    this.newDate = newDate;
  }

  public LocalDate getPreviousDate() {
    return previousDate;
  }

  public void setPreviousDate(LocalDate previousDate) {
    this.previousDate = previousDate;
  }

}
