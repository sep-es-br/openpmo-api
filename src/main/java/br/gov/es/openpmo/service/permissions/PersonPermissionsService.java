package br.gov.es.openpmo.service.permissions;

import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonPermissionsService {

    private final PersonRepository personRepository;
    private final IsCCBMemberRepository ccbMemberRepository;

    @Autowired
    public PersonPermissionsService(
            final PersonRepository personRepository,
            IsCCBMemberRepository ccbMemberRepository
    ) {
        this.personRepository = personRepository;
        this.ccbMemberRepository = ccbMemberRepository;
    }

    @Transactional
    public void deleteAllPermissions(final Long idPerson, final Long idOffice) {
        this.ccbMemberRepository.deleteAllByPersonIdAndOfficeId(idPerson, idOffice);
        this.personRepository.deleteAllPermissionsBy(idPerson, idOffice);
    }


}
