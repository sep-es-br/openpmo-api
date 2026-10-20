package br.gov.es.openpmo.service.office;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalContextDto;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.OfficePermissionRepository;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.TransversalRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllOfficeUsingCustomFilter;
import br.gov.es.openpmo.service.actors.PersonService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OfficeServicePermissionFilterTest {

  private static final Long PERSON_ID = 1086113L;
  private static final String USER_KEY = "renata-key";
  private OfficePermissionRepository officePermissionRepository;
  private TransversalRepository transversalRepository;
  private PersonService personService;
  private OfficeService officeService;

  @Before
  public void setUp() {
    final OfficeRepository officeRepository = mock(OfficeRepository.class);
    this.personService = mock(PersonService.class);
    this.officePermissionRepository = mock(OfficePermissionRepository.class);
    final CustomFilterRepository customFilterRepository = mock(CustomFilterRepository.class);
    final FindAllOfficeUsingCustomFilter findAllOffice = mock(FindAllOfficeUsingCustomFilter.class);
    final AppProperties appProperties = mock(AppProperties.class);
    this.transversalRepository = mock(TransversalRepository.class);
    this.officeService = new OfficeService(
      officeRepository,
      this.personService,
      this.officePermissionRepository,
      customFilterRepository,
      findAllOffice,
      appProperties,
      this.transversalRepository
    );

    final Person person = mock(Person.class);
    when(this.personService.findById(PERSON_ID)).thenReturn(person);
    when(person.getAdministrator()).thenReturn(false);
  }

  @Test
  public void shouldKeepOnlyOfficesWithPermissionOrStakeholderViewContext() {
    final List<OfficeDto> offices = new ArrayList<>(Arrays.asList(office(1L), office(2L), office(3L)));
    when(this.officePermissionRepository.findByIdOfficeAndIdPerson(1L, PERSON_ID))
      .thenReturn(new ArrayList<>());
    when(this.officePermissionRepository.findByIdOfficeAndIdPerson(2L, PERSON_ID))
      .thenReturn(new ArrayList<>());
    when(this.officePermissionRepository.findByIdOfficeAndIdPerson(3L, PERSON_ID))
      .thenReturn(new ArrayList<>());
    when(this.officePermissionRepository.hasCanAccessPlan(1L, PERSON_ID)).thenReturn(false);
    when(this.officePermissionRepository.hasCanAccessWorkpack(1L, PERSON_ID)).thenReturn(false);
    when(this.officePermissionRepository.hasCanAccessPlan(2L, PERSON_ID)).thenReturn(false);
    when(this.officePermissionRepository.hasCanAccessWorkpack(2L, PERSON_ID)).thenReturn(false);
    when(this.officePermissionRepository.hasCanAccessPlan(3L, PERSON_ID)).thenReturn(false);
    when(this.officePermissionRepository.hasCanAccessWorkpack(3L, PERSON_ID)).thenReturn(false);
    when(this.transversalRepository.findStakeholderAccessContexts(USER_KEY))
      .thenReturn(Collections.singletonList(context(2L)));
    when(this.transversalRepository.findDirectStructuralAccessContexts(USER_KEY))
      .thenReturn(Collections.singletonList(context(3L)));

    final List<OfficeDto> result = this.officeService.checkPermission(offices, PERSON_ID, USER_KEY);

    assertEquals(Arrays.asList(2L, 3L), Arrays.asList(result.get(0).getId(), result.get(1).getId()));
  }

  private static OfficeDto office(final Long id) {
    final OfficeDto office = new OfficeDto();
    office.setId(id);
    return office;
  }

  private static TransversalContextDto context(final Long idOffice) {
    final TransversalContextDto context = new TransversalContextDto();
    context.setIdOffice(idOffice);
    return context;
  }
}
