package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.dto.plan.PlanUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.Plan;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.repository.PlanPermissionRepository;
import br.gov.es.openpmo.repository.PlanRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final PersonService personService;
    private final PlanPermissionRepository planPermissionRepository;

    @Autowired
    public PlanService(PlanRepository planRepository, PersonService personService, PlanPermissionRepository planPermissionRepository) {
        this.planRepository = planRepository;
        this.personService = personService;
        this.planPermissionRepository = planPermissionRepository;
    }

    public List<Plan> findAll() {
        List<Plan> plans = new ArrayList<>();
        planRepository.findAll().forEach(plans::add);
        return plans;
    }

    public List<Plan> findAllInOffice(Long id) {
        return planRepository.findAllInOffice(id);
    }

    public Plan save(Plan plan) {
        return planRepository.save(plan);
    }

    public Plan findById(Long id) {
        return planRepository.findById(id).orElseThrow(() ->
                new NegocioException(ApplicationMessage.PLAN_NOT_FOUND));
    }

    public void delete(Plan plan) {
        planRepository.delete(plan);
    }

    public Plan getPlan(PlanUpdateDto planUpdateDto) {
        Plan plan = findById(planUpdateDto.getId());
        plan.setStart(planUpdateDto.getStart());
        plan.setFinish(planUpdateDto.getFinish());
        plan.setName(planUpdateDto.getName());
        plan.setFullName(planUpdateDto.getFullName());
        return plan;
    }

    public List<PlanDto> chekPermission(List<PlanDto> plans, Long idUser) {
        Person person = personService.findById(idUser);
        if (person.isAdministrator()) {
            return plans;
        }
        for (Iterator<PlanDto> it = plans.iterator(); it.hasNext();) {
            PlanDto planDto = it.next();
            List<CanAccessPlan> canAccessPlans = planPermissionRepository.findByIdPlanAndIdPerson(planDto.getId(), idUser);
            if (canAccessPlans.isEmpty()) {
                it.remove();
                continue;
            }
            planDto.setPermissions(new ArrayList<>());
            canAccessPlans.forEach(p -> {
                PermissionDto dto = new PermissionDto();
                dto.setId(p.getId());
                dto.setLevel(p.getPermissionLevel());
                dto.setRole(p.getPermitedRole());
                planDto.getPermissions().add(dto);
            });
        }
        return plans;
    }
}

