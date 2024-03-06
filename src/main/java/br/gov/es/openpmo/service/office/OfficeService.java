package br.gov.es.openpmo.service.office;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.OfficePermissionRepository;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllOfficeUsingCustomFilter;
import br.gov.es.openpmo.service.actors.PersonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static br.gov.es.openpmo.utils.ApplicationMessage.CUSTOM_FILTER_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.OFFICE_DELETE_RELATIONSHIP_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.OFFICE_NOT_FOUND;

@Service
public class OfficeService {

  private final OfficeRepository officeRepository;
  private final PersonService personService;
  private final OfficePermissionRepository officePermissionRepository;
  private final CustomFilterRepository customFilterRepository;
  private final FindAllOfficeUsingCustomFilter findAllOffice;
  private final AppProperties appProperties;

  @Autowired
  public OfficeService(
    final OfficeRepository officeRepository,
    final PersonService personService,
    final OfficePermissionRepository officePermissionRepository,
    final CustomFilterRepository customFilterRepository,
    final FindAllOfficeUsingCustomFilter findAllOffice,
    final AppProperties appProperties
  ) {
    this.officeRepository = officeRepository;
    this.personService = personService;
    this.officePermissionRepository = officePermissionRepository;
    this.customFilterRepository = customFilterRepository;
    this.findAllOffice = findAllOffice;
    this.appProperties = appProperties;
  }

  public List<Office> findAll(final Long idFilter, final String term) {

    if(idFilter == null) {
    	if (StringUtils.isBlank(term)) return this.findAll();
    	else return findByNameOrFullName(term);
    }

    final CustomFilter filter = this.customFilterRepository
      .findById(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));
    Map<String, Object> map = new HashMap<>();
    map.put("term", term);
    map.put("searchCutOffScore", appProperties.getSearchCutOffScore());
    if (StringUtils.isNotBlank(term)) {
    	filter.setSimilarityFilter(true);
    }
    return this.findAllOffice.execute(filter, map);
  }

	public List<Office> findByNameOrFullName(String name) {
		return this.officeRepository.findAllOfficeByNameOrFullName(name,
                appProperties.getSearchCutOffScore());
	}

  public List<Office> findAll() {
    final List<Office> offices = new ArrayList<>();
    this.officeRepository.findAll().forEach(offices::add);
    offices.sort(Comparator.comparing(Office::getName));
    return offices;
  }

  public Office save(final Office office) {
    return this.officeRepository.save(office, 0);
  }

  public Office findById(final Long id) {
    return this.officeRepository.findById(id).orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));
  }

  public Office findByIdThin(final Long id) {
    return this.officeRepository.findByIdThin(id).orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));
  }

  public Optional<Office> maybeFindById(final Long id) {
    return this.officeRepository.findById(id);
  }

  public void delete(final Office office) {
    if((office.getPlans() != null && !office.getPlans().isEmpty())
       || (office.getPlansModel() != null && !office.getPlansModel().isEmpty())) {
      throw new NegocioException(OFFICE_DELETE_RELATIONSHIP_ERROR);
    }
    this.officeRepository.delete(office);
  }

  public List<OfficeDto> checkPermission(
    final List<OfficeDto> offices,
    final Long idUser
  ) {
    final Person person = this.personService.findById(idUser);
    if(person.getAdministrator()) {
      return offices;
    }
    for(final Iterator<OfficeDto> it = offices.iterator(); it.hasNext(); ) {
      final OfficeDto officeDto = it.next();
      final Long officeId = officeDto.getId();
      final List<CanAccessOffice> canAccessOffices = this.officePermissionRepository.findByIdOfficeAndIdPerson(
        officeId,
        idUser
      );
      canAccessOffices.removeIf(c -> c.getPermissionLevel() == PermissionLevelEnum.NONE);
      if(canAccessOffices.isEmpty()) {
        if(!this.hasPermissionPlanWorkpack(officeId, idUser)) {
          it.remove();
          continue;
        }
      }
      if(!canAccessOffices.isEmpty()) {
        officeDto.setPermissions(new ArrayList<>());
        canAccessOffices.forEach(canAcessOffice -> {
          final PermissionDto dto = new PermissionDto();
          dto.setId(canAcessOffice.getId());
          dto.setLevel(canAcessOffice.getPermissionLevel());
          dto.setRole(canAcessOffice.getRole());
          officeDto.getPermissions().add(dto);
        });
        continue;
      }
      officeDto.setPermissions(new ArrayList<>());
      final PermissionDto dto = new PermissionDto();
      dto.setId(0L);
      dto.setLevel(PermissionLevelEnum.READ);
      dto.setRole("user");
      officeDto.getPermissions().add(dto);

    }
    return offices;
  }

  public boolean hasPermissionPlanWorkpack(
    final Long idOffice,
    final Long idUser
  ) {
    final boolean hasCanAccessPlan = this.officePermissionRepository.hasCanAccessPlan(idOffice, idUser);
    if(hasCanAccessPlan) {
      return true;
    }
    return this.officePermissionRepository.hasCanAccessWorkpack(idOffice, idUser);
  }

  public Optional<Office> findOfficeByPlan(final Long id) {
    return this.officeRepository.findOfficeByPlanId(id);
  }

  public Set<Office> findAllByIds(final Iterable<Long> ids) {
    final Iterable<Office> offices = this.officeRepository.findAllById(ids);
    final Spliterator<Office> spliterator = offices.spliterator();
    return StreamSupport.stream(spliterator, false).collect(Collectors.toSet());
  }

}
