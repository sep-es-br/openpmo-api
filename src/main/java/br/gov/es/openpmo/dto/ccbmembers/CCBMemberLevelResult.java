package br.gov.es.openpmo.dto.ccbmembers;

import br.gov.es.openpmo.model.actors.Person;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class CCBMemberLevelResult {

  private Long idPerson;
  private Person person;
  private String role;
  private String workLocation;
  private Boolean active;
  private String level;     
  private Long idLevel;    
  private String levelName; 

  public Long getIdPerson() {
    return this.idPerson;
  }

  public void setIdPerson(final Long idPerson) {
    this.idPerson = idPerson;
  }

  public Person getPerson() {
    return this.person;
  }

  public void setPerson(final Person person) {
    this.person = person;
  }

  public String getRole() {
    return this.role;
  }

  public void setRole(final String role) {
    this.role = role;
  }

  public String getWorkLocation() {
    return this.workLocation;
  }

  public void setWorkLocation(final String workLocation) {
    this.workLocation = workLocation;
  }

  public Boolean getActive() {
    return this.active;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }

  public String getLevel() {
    return this.level;
  }

  public void setLevel(final String level) {
    this.level = level;
  }

  public Long getIdLevel() {
    return this.idLevel;
  }

  public void setIdLevel(final Long idLevel) {
    this.idLevel = idLevel;
  }

  public String getLevelName() {
    return this.levelName;
  }

  public void setLevelName(final String levelName) {
    this.levelName = levelName;
  }

}
