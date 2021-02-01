package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Office;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.repository.OfficePermissionRepository;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class OfficeService {

    private final OfficeRepository officeRepository;
    private final PersonService personService;
    private final OfficePermissionRepository officePermissionRepository;

    @Autowired
    public OfficeService(OfficeRepository officeRepository, PersonService personService
        , OfficePermissionRepository officePermissionRepository) {
        this.officeRepository = officeRepository;
        this.personService = personService;
        this.officePermissionRepository = officePermissionRepository;
    }

    public List<Office> findAll() {
        List<Office> offices = new ArrayList<>();
        officeRepository.findAll().forEach(offices::add);
        return offices;
    }

    public Office save(Office office) {
        return officeRepository.save(office);
    }

    public Office findById(Long id) {
        return officeRepository.findById(id).orElseThrow(() -> new NegocioException(ApplicationMessage.OFFICE_NOT_FOUND));
    }

    public void delete(Office office) {
        if ((office.getPlans() != null && !office.getPlans().isEmpty())
            ||(office.getPlansModel() != null && !office.getPlansModel().isEmpty()) ) {
            throw new NegocioException(ApplicationMessage.OFFICE_DELETE_RELATIONSHIP_ERROR);
        }
        officeRepository.delete(office);
    }

    public List<OfficeDto> chekPermission(List<OfficeDto> offices, Long idUser) {
        Person person = personService.findById(idUser);
        if (person.isAdministrator()) {
            return offices;
        }
        for(Iterator<OfficeDto> it = offices.iterator(); it.hasNext();) {
            OfficeDto officeDto = it.next();
            List<CanAccessOffice>  canAccessOffices = officePermissionRepository.findByIdOfficeAndIdPerson(officeDto.getId(), idUser);
            if(canAccessOffices.isEmpty()) {
                it.remove();
                continue;
            }
            officeDto.setPermissions(new ArrayList<>());
            canAccessOffices.forEach(canAcessOffice -> {
                PermissionDto dto = new PermissionDto();
                dto.setId(canAcessOffice.getId());
                dto.setLevel(canAcessOffice.getPermissionLevel());
                dto.setRole(canAcessOffice.getPermitedRole());
                officeDto.getPermissions().add(dto);
            });
        }
        return offices;
    }
}
