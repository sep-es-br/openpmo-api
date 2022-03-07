package br.gov.es.openpmo.model.actors;

import br.gov.es.openpmo.dto.ccbmembers.PersonResponse;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@NodeEntity
public class Person extends Actor {

    private Boolean administrator;

    @Relationship(type = "IS_AUTHENTICATED_BY")
    private Set<IsAuthenticatedBy> authentications;

    @Relationship(type = "IS_IN_CONTACT_BOOK_OF")
    private Set<IsInContactBookOf> isInContactBookOf;

    @Relationship(type = "IS_A_PORTRAIT_OF", direction = Relationship.INCOMING)
    private File avatar;

    public Person() {
    }

    @Transient
    public Optional<IsAuthenticatedBy> findAuthenticationDataBy(final String serverName) {
        if (this.authentications == null) return Optional.empty();
        return this.authentications
                .stream()
                .filter(auth -> auth.getAuthService().getServer().equalsIgnoreCase(serverName))
                .findFirst();
    }

    @Transient
    public Optional<IsInContactBookOf> findContactInformationBy(final Long idOffice) {
        if (this.authentications == null) return Optional.empty();
        return this.isInContactBookOf
                .stream()
                .filter(contact -> contact.getOfficeId().equals(idOffice))
                .findFirst();
    }

    public Boolean getAdministrator() {
        return this.administrator;
    }

    public void setAdministrator(final Boolean administrator) {
        this.administrator = administrator;
    }

    public Set<IsAuthenticatedBy> getAuthentications() {
        if (this.authentications == null) return new HashSet<>();
        return Collections.unmodifiableSet(this.authentications);
    }

    public void setAuthentications(final Set<IsAuthenticatedBy> authentications) {
        this.authentications = authentications;
    }

    public Set<IsInContactBookOf> getIsInContactBookOf() {
        if (this.isInContactBookOf == null) return new HashSet<>();
        return Collections.unmodifiableSet(this.isInContactBookOf);
    }

    public void setIsInContactBookOf(final Set<IsInContactBookOf> isInContactBookOf) {
        this.isInContactBookOf = isInContactBookOf;
    }

    public File getAvatar() {
        return this.avatar;
    }

    public void setAvatar(final File avatar) {
        this.avatar = avatar;
    }

    public PersonResponse getPersonResponse() {
        final PersonResponse personResponse = new PersonResponse();

        personResponse.setId(this.getId());
        personResponse.setName(this.getName());
        personResponse.setFullName(this.getFullName());

        this.getIsInContactBookOfOrEmptySet().stream().findFirst().ifPresent(contactBook -> {
            personResponse.setAddress(contactBook.getAddress());
            personResponse.setContactEmail(contactBook.getEmail());
            personResponse.setPhoneNumber(contactBook.getPhoneNumber());
        });

        return personResponse;
    }

    private Set<IsInContactBookOf> getIsInContactBookOfOrEmptySet() {
        return Optional.ofNullable(this.isInContactBookOf).orElse(Collections.emptySet());
    }

}
