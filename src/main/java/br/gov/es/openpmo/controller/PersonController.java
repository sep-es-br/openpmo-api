package br.gov.es.openpmo.controller;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.service.PersonService;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/person")
public class PersonController {

    private PersonService personService;
    private ModelMapper modelMapper;

    @Autowired
    public PersonController(PersonService personService, ModelMapper modelMapper) {
        this.personService = personService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{email}")
    public ResponseEntity<ResponseBase<PersonDto>> findByEmail(@PathVariable String email) {
        Optional<Person> personOptional = personService.findByEmail(email);
        if (!personOptional.isPresent())
            return ResponseEntity.noContent().build();

        PersonDto personDto = modelMapper.map(personOptional.get(), PersonDto.class);
        ResponseBase<PersonDto> response = new ResponseBase<PersonDto>().setData(personDto).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }
}
