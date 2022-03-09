package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.apis.acessocidadao.AcessoCidadaoApi;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentEmailResponse;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentResponse;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentRoleResponse;
import br.gov.es.openpmo.dto.person.CitizenByNameQuery;
import br.gov.es.openpmo.dto.person.CitizenDto;
import br.gov.es.openpmo.dto.person.CitizenDtoBuilder;
import br.gov.es.openpmo.dto.person.RoleResource;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.CITIZEN_EMAIL_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.CITIZEN_NOT_FOUND;

@Service
public class CitizenService {

    private final AcessoCidadaoApi acessoCidadaoApi;

    private final PersonService personService;

    private final IsInContactBookOfService contactService;

    @Value("${app.login.server.name}")
    private String serverName;

    @Autowired
    public CitizenService(
            final AcessoCidadaoApi acessoCidadaoApi,
            final PersonService personService,
            final IsInContactBookOfService contactService
    ) {
        this.acessoCidadaoApi = acessoCidadaoApi;
        this.personService = personService;
        this.contactService = contactService;
    }

    private static int orderByAgentName(final CitizenByNameQuery agent1, final CitizenByNameQuery agent2) {
        return agent1.getName().compareToIgnoreCase(agent2.getName());
    }

    private static boolean isAgentNameContainedInQueryName(final String name, final PublicAgentResponse agent) {
        return agent.getName().toLowerCase().contains(name.toLowerCase(Locale.ROOT));
    }

    private static CitizenDto buildDtoFromPublicAgent(
            final PublicAgentEmailResponse publicAgentEmailResponse,
            final PublicAgentResponse agent, final List<RoleResource> roles
    ) {
        final String[] nameSplited = agent.getName().split(" ");
        return CitizenDtoBuilder.aCitizenDto()
                .withName(nameSplited.length == 0 ? agent.getName() : nameSplited[0])
                .withEmail(publicAgentEmailResponse.getEmail())
                .withContactEmail(publicAgentEmailResponse.getCorporateEmail())
                .withFullName(agent.getName())
                .withRoles(roles)
                .build();
    }

    @Transactional
    public List<CitizenByNameQuery> findPersonByName(final String name, final Long idPerson) {
        final List<PublicAgentResponse> publicAgentResponses = this.acessoCidadaoApi
                .findAllPublicAgents(idPerson)
                .stream()
                .filter(agent -> isAgentNameContainedInQueryName(name, agent))
                .collect(Collectors.toList());

        return publicAgentResponses.stream()
                .filter(Objects::nonNull)
                .map(agent -> new CitizenByNameQuery(agent.getName(), agent.getSub()))
                .sorted(CitizenService::orderByAgentName)
                .collect(Collectors.toList());
    }

    public CitizenDto findPersonByCpf(final String cpf, final Long idOffice, final Long idPerson) {
        final String sub = this.findSubByCpf(cpf, idPerson);

        final Optional<PublicAgentResponse> maybeAgent = this.acessoCidadaoApi.findPublicAgentBySub(sub, idPerson);

        return maybeAgent.isPresent() ?
                this.processPublicAgent(maybeAgent.get(), idOffice, idPerson) :
                this.processNonPublicAgent(sub, idOffice, idPerson);
    }

    private String findSubByCpf(final String cpf, final Long idPerson) {
        return this.acessoCidadaoApi
                .findSubByCpf(cpf, idPerson)
                .orElseThrow(() -> new NegocioException(CITIZEN_NOT_FOUND));
    }

    private CitizenDto processPublicAgent(final PublicAgentResponse agent, final Long idOffice, final Long idPerson) {
        return this.toCitizenDto(agent, idOffice, idPerson);
    }

    private CitizenDto toCitizenDto(final PublicAgentResponse agent, final Long idOffice, final Long idPerson) {
        final Optional<PublicAgentEmailResponse> maybeAgentEmail = this.acessoCidadaoApi.findAgentEmail(agent.getSub(), idPerson);

        final List<RoleResource> roles = this.acessoCidadaoApi.findRoles(agent.getSub(), idPerson)
                .stream()
                .map(roleResponse -> this.getRoleDto(roleResponse, idPerson))
                .collect(Collectors.toList());

        if (!maybeAgentEmail.isPresent()) {
            return null;
        }

        final PublicAgentEmailResponse publicAgentEmailResponse = maybeAgentEmail.get();

        final Optional<Person> maybePerson = this.personService.findByEmail(publicAgentEmailResponse.getEmail());

        return this.buildCitizenDto(agent, roles, publicAgentEmailResponse, maybePerson, idOffice);
    }

    private RoleResource getRoleDto(final PublicAgentRoleResponse roleResponse, final Long idPerson) {
        final RoleResource roleResource = new RoleResource();
        roleResource.setRole(roleResponse.getName());

        final String workLocation = this.acessoCidadaoApi.getWorkLocation(roleResponse.getOrganizationGuid(), idPerson);
        roleResource.setWorkLocation(workLocation);

        return roleResource;
    }

    private CitizenDto buildCitizenDto(
            final PublicAgentResponse agent, final List<RoleResource> roles,
            final PublicAgentEmailResponse publicAgentEmailResponse, final Optional<Person> maybePerson, final Long idOffice
    ) {
        if (!maybePerson.isPresent()) {
            return buildDtoFromPublicAgent(
                    publicAgentEmailResponse,
                    agent,
                    roles
            );
        }
        final Person person = maybePerson.get();
        return this.buildDtoFromCitizenAlreadyRegistered(
                roles,
                person,
                idOffice
        );
    }

    private CitizenDto buildDtoFromCitizenAlreadyRegistered(final List<RoleResource> roles, final Person person, final Long idOffice) {
        final CitizenDtoBuilder builder = CitizenDtoBuilder.aCitizenDto()
                .withId(person.getId())
                .withName(person.getName())
                .withFullName(person.getFullName())
                .withRoles(roles)
                .withAdministrator(person.getAdministrator());

        if (idOffice != null) {
            final Optional<IsInContactBookOf> maybeContactInformation = this.contactService.findContactInformationUsingPersonIdAndOffice(
                    person.getId(),
                    idOffice
            );
            maybeContactInformation.ifPresent(contact -> {
                builder.withContactEmail(contact.getEmail())
                        .withAddress(contact.getAddress())
                        .withPhoneNumber(contact.getPhoneNumber());
            });
        }

        person.findAuthenticationDataBy(this.serverName)
                .ifPresent(auth -> builder.withEmail(auth.getEmail()));

        return builder.build();
    }

    private CitizenDto processNonPublicAgent(final String sub, final Long idOffice, final Long idPerson) {
        final PublicAgentEmailResponse agentEmail = this.findAgentEmail(sub, idPerson);
        final Optional<Person> maybePerson = this.personService.findByEmail(agentEmail.getEmail());

        final List<RoleResource> roles = this.acessoCidadaoApi.findRoles(sub, idPerson)
                .stream()
                .map(roleResponse -> this.getRoleDto(roleResponse, idPerson))
                .collect(Collectors.toList());

        if (maybePerson.isPresent()) {
            return this.buildDtoFromCitizenAlreadyRegistered(roles, maybePerson.get(), idOffice);
        }

        final String[] parsedName = agentEmail.getEmail().split("@");
        return CitizenDtoBuilder.aCitizenDto()
                .withName(parsedName.length == 0 ? agentEmail.getEmail() : parsedName[0])
                .withFullName(parsedName.length == 0 ? agentEmail.getEmail() : parsedName[0])
                .withEmail(agentEmail.getEmail())
                .withContactEmail(agentEmail.getCorporateEmail())
                .withIsUser(false)
                .withRoles(roles)
                .build();
    }

    private PublicAgentEmailResponse findAgentEmail(final String sub, final Long idPerson) {
        return this.acessoCidadaoApi
                .findAgentEmail(sub, idPerson)
                .orElseThrow(() -> new NegocioException(CITIZEN_EMAIL_NOT_FOUND));
    }

    public CitizenDto findCitizenBySub(final String sub, final Long idOffice, final Long idPerson) {
        final PublicAgentResponse agent = this.acessoCidadaoApi.findPublicAgentBySub(sub, idPerson)
                .orElseThrow(() -> new NegocioException(CITIZEN_NOT_FOUND));

        return this.toCitizenDto(agent, idOffice, idPerson);
    }

}
