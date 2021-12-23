package br.gov.es.openpmo.model.actors;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.journals.JournalEntry;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NodeEntity
public class File extends Entity {

  public static final char DOT = '.';

  private static final Pattern SPACE_CHAR = Pattern.compile(" ", Pattern.LITERAL);

  private String mimeType;

  private String userGivenName;

  private String uniqueNameKey;

  @Relationship(type = "IS_A_PORTRAIT_OF")
  private Person person;

  @Relationship("IS_EVIDENCE_OF")
  private JournalEntry journalEntry;

  public Person getPerson() {
    return this.person;
  }

  public void setPerson(final Person person) {
    this.person = person;
  }

  public String getMimeType() {
    return this.mimeType;
  }

  public void setMimeType(final String mimeType) {
    this.mimeType = mimeType;
  }

  public String getUniqueNameKey() {
    return this.uniqueNameKey;
  }

  public void setUniqueNameKey(final CharSequence uniqueNameKey) {
    this.uniqueNameKey = SPACE_CHAR.matcher(uniqueNameKey).replaceAll(Matcher.quoteReplacement("-"));
  }

  @Transient
  public String getExtension() {
    final int index = this.userGivenName.lastIndexOf(DOT);
    return index == -1 ? "" : this.userGivenName.substring(index);
  }

  public String getUserGivenName() {
    return this.userGivenName;
  }

  public void setUserGivenName(final String userGivenName) {
    this.userGivenName = userGivenName;
  }

  public JournalEntry getJournalEntry() {
    return this.journalEntry;
  }

  public void setJournalEntry(final JournalEntry journalEntry) {
    this.journalEntry = journalEntry;
  }

}
