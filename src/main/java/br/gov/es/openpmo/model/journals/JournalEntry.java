package br.gov.es.openpmo.model.journals;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
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

  private PermissionLevelEnum level;

  @Relationship("SCOPE_TO")
  private Workpack workpack;

  @Relationship("SCOPE_TO")
  private Office office;

  @Relationship("SCOPE_TO")
  private Plan plan;

  @Relationship("IS_RECORDED_BY")
  private Person author;

  @Relationship("IS_RECORDED_FOR")
  private Person target;

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
    final Person author
  ) {
    this.type = type;
    this.action = action;
    this.nameItem = nameItem;
    this.description = description;
    this.reason = reason;
    this.newDate = newDate;
    this.previousDate = previousDate;
    this.workpack = workpack;
    this.author = author;
    this.date = LocalDateTime.now();
  }

  public static JournalEntry of(
    final JournalType type,
    final JournalAction action,
    final PermissionLevelEnum level,
    final String nameItem,
    final String description,
    final Office office,
    final Person target,
    final Person author
  ) {
    final JournalEntry journalEntry = new JournalEntry();
    journalEntry.setType(type);
    journalEntry.setAction(action);
    journalEntry.setLevel(level);
    journalEntry.setNameItem(nameItem);
    journalEntry.setDescription(description);
    journalEntry.setOffice(office);
    journalEntry.setAuthor(author);
    journalEntry.setTarget(target);
    journalEntry.setDate(LocalDateTime.now());
    return journalEntry;
  }

  public static JournalEntry of(
    final JournalType type,
    final JournalAction action,
    final PermissionLevelEnum level,
    final String nameItem,
    final String description,
    final Plan plan,
    final Person target,
    final Person author
  ) {
    final JournalEntry journalEntry = new JournalEntry();
    journalEntry.setType(type);
    journalEntry.setAction(action);
    journalEntry.setLevel(level);
    journalEntry.setNameItem(nameItem);
    journalEntry.setDescription(description);
    journalEntry.setPlan(plan);
    journalEntry.setAuthor(author);
    journalEntry.setTarget(target);
    journalEntry.setDate(LocalDateTime.now());
    return journalEntry;
  }

  public static JournalEntry of(
    final JournalType type,
    final JournalAction action,
    final PermissionLevelEnum level,
    final String nameItem,
    final String description,
    final Workpack workpack,
    final Person target,
    final Person author
  ) {
    final JournalEntry journalEntry = new JournalEntry();
    journalEntry.setType(type);
    journalEntry.setAction(action);
    journalEntry.setLevel(level);
    journalEntry.setNameItem(nameItem);
    journalEntry.setDescription(description);
    journalEntry.setWorkpack(workpack);
    journalEntry.setAuthor(author);
    journalEntry.setTarget(target);
    journalEntry.setDate(LocalDateTime.now());
    return journalEntry;
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

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  @Transient
  public Long getWorkpackId() {
    return Optional.ofNullable(this.workpack).map(Workpack::getId).orElse(null);
  }

  public Office getOffice() {
    return this.office;
  }

  public void setOffice(final Office office) {
    this.office = office;
  }

  public Plan getPlan() {
    return this.plan;
  }

  public void setPlan(final Plan plan) {
    this.plan = plan;
  }

  public Person getAuthor() {
    return this.author;
  }

  public void setAuthor(final Person author) {
    this.author = author;
  }

  public Person getTarget() {
    return this.target;
  }

  public void setTarget(final Person target) {
    this.target = target;
  }

  @Transient
  public Long getPersonId() {
    return Optional.ofNullable(this.author).map(Person::getId).orElse(null);
  }

  @Transient
  public String getPersonName() {
    return Optional.ofNullable(this.author).map(Person::getName).orElse(null);
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
    return this.reason;
  }

  public void setReason(final String reason) {
    this.reason = reason;
  }

  public LocalDate getNewDate() {
    return this.newDate;
  }

  public void setNewDate(final LocalDate newDate) {
    this.newDate = newDate;
  }

  public LocalDate getPreviousDate() {
    return this.previousDate;
  }

  public void setPreviousDate(final LocalDate previousDate) {
    this.previousDate = previousDate;
  }

  public PermissionLevelEnum getLevel() {
    return this.level;
  }

  public void setLevel(final PermissionLevelEnum level) {
    this.level = level;
  }

}
