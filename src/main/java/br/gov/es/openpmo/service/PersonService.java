package br.gov.es.openpmo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.domain.TokenType;
import br.gov.es.openpmo.repository.PersonRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.jsonwebtoken.Claims;

@Service
public class PersonService {

    private TokenService tokenService;
    private PersonRepository personRepository;

    @Value("${users.administrators}")
    private List<String> administrators;

    @Autowired
    public PersonService(PersonRepository personRepository, TokenService tokenService) {
        this.tokenService = tokenService;
        this.personRepository = personRepository;
    }

    public Person findByAuthorizationHeader(String authorizationHeader) {

        Claims user = tokenService.getUser(authorizationHeader.split(" ")[1], TokenType.AUTHENTICATION);

        String email = (String) user.get("email");

        return personRepository.findByEmail(email)
                .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
    }

    public Optional<Person> findByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    public Person findByEmailWithException(String email) {
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
    }

    public Person save(Person person) {
        return personRepository.save(person);
    }

    public Person findById(Long id) {
        return personRepository.findById(id).orElse(null);
    }

    public List<Person> personInCanAccessOffice(Long idOffice) {
        return personRepository.findByIdOfficeReturnDistinctPerson(idOffice);
    }

    public List<Person> personInCanAccessPlan(Long idPlan) {
        return personRepository.findByIdPlanReturnDistinctPerson(idPlan);
    }

    public List<Person> personInIsStakeholderIn(Long idWorkpack) {
		return personRepository.findByIdWorkpackReturnDistinctPerson(idWorkpack);
	}

    public Person savePersonByEmail(String email) {
        String[] name = email.split("@");
        Person person = new Person();
        person.setAdministrator(administrators.contains(email));
        person.setContactEmail(email);
        person.setEmail(email);
        person.setName(name.length == 0 ? email : name[0]);
        return save(person);
    }
}
