package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionDto;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionParamDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.Plan;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.repository.PlanPermissionRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class PlanPermissionService {

    private final PlanPermissionRepository repository;
    private final PlanService planService;
    private final PersonService personService;
    private final ModelMapper modelMapper;

    @Autowired
    public PlanPermissionService(PlanPermissionRepository repository, PlanService planService,
            PersonService personService, ModelMapper modelMapper) {
        this.repository = repository;
        this.planService = planService;
        this.personService = personService;
        this.modelMapper = modelMapper;
    }

    public void delete(Long idPlan, String email) {
        Optional<Person> personOptional = personService.findByEmail(email);
        if (!personOptional.isPresent())
            throw new RegistroNaoEncontradoException(ApplicationMessage.PLAN_PERMISSION_NOT_FOUND);
        List<CanAccessPlan> list = repository.findByIdPlanAndIdPerson(idPlan, personOptional.get().getId());
        repository.deleteAll(list);
    }

    public List<PlanPermissionDto> findAllDto(Long idPlan, String email) {
        List<PlanPermissionDto> plansPermissionDto = new ArrayList<>();
        Plan plan = planService.findById(idPlan);
        List<CanAccessPlan> listPlansPermission = listPlansPermissions(plan, email);
        Map<Person, List<PermissionDto>> mapPermission = new HashMap<>();

        listPlansPermission.forEach(p -> {
            PermissionDto dto = new PermissionDto();
            dto.setId(p.getId());
            dto.setLevel(p.getPermissionLevel());
            dto.setRole(p.getPermitedRole());
            mapPermission.computeIfAbsent(p.getPerson(), k -> new ArrayList<>());
            mapPermission.get(p.getPerson()).add(dto);
        });

        mapPermission.keySet().forEach(k -> {
            PlanPermissionDto planPermissionDto = new PlanPermissionDto();
            planPermissionDto.setIdPlan(idPlan);
            planPermissionDto.setPerson(this.modelMapper.map(k, PersonDto.class));
            planPermissionDto.setPermissions(mapPermission.get(k));
            plansPermissionDto.add(planPermissionDto);
        });

        return plansPermissionDto;
    }

    private List<CanAccessPlan> listPlansPermissions(Plan plan, String email) {
        if (email == null || email.isEmpty())
            return findByIdPlan(plan.getId());

        Person person = personService.findByEmailWithException(email);

        return findByPlanAndPerson(plan, person);
    }

    public List<CanAccessPlan> findByIdPlan(Long idPlan) {
        return repository.findByIdPlan(idPlan);
    }

    public List<CanAccessPlan> findByPlanAndPerson(Plan plan, Person person) {
        return repository.findByIdPlanAndIdPerson(plan.getId(), person.getId());
    }

    public CanAccessPlan save(CanAccessPlan planPermission) {
        return repository.save(planPermission);
    }

    public CanAccessPlan findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NegocioException(ApplicationMessage.PLAN_PERMISSION_NOT_FOUND));
    }

    public void delete(CanAccessPlan plan) {
        repository.delete(plan);
    }

    public void update(PlanPermissionParamDto request) {
        Person person = returnPersonBuild(request.getEmail());
        Plan plan = planService.findById(request.getIdPlan());
        List<CanAccessPlan> plansPermissionsDataBase = findByPlanAndPerson(plan, person);

        plansPermissionsDataBase.forEach(permissionDatabase -> {
            boolean find = request.getPermissions() != null && request.getPermissions().stream()
                    .anyMatch(filtro -> permissionDatabase.getId().equals(filtro.getId()));
            if (find)
                return;
            delete(permissionDatabase);
        });

        if (request.getPermissions() != null) {
            request.getPermissions().forEach(permission -> {
                if (permission.getId() == null) {
                    save(buildCanAcessPlan(person, plan, permission, null));
                    return;
                }

                Optional<CanAccessPlan> optionalCanAccessPlan = repository.findById(permission.getId());
                if (!optionalCanAccessPlan.isPresent()) {
                    throw new RegistroNaoEncontradoException(ApplicationMessage.PLAN_PERMISSION_NOT_FOUND);
                }
                save(buildCanAcessPlan(person, plan, permission, optionalCanAccessPlan.get().getId()));
            });
        }

    }

    public void store(PlanPermissionParamDto request) {
        Person person = returnPersonBuild(request.getEmail());
        Plan plan = planService.findById(request.getIdPlan());
        request.getPermissions().forEach(permission -> save(buildCanAcessPlan(person, plan, permission, null)));
    }

    private CanAccessPlan buildCanAcessPlan(Person person, Plan plan, PermissionDto request, Long id) {
        return new CanAccessPlan(id, "", request.getRole(), request.getLevel(), person, plan);
    }

    private Person returnPersonBuild(String email) {
        Optional<Person> personOptional = personService.findByEmail(email);
        return personOptional.orElseGet(() -> storePerson(email));

    }

    private Person storePerson(String email) {
        return personService.savePersonByEmail(email);
    }

}
