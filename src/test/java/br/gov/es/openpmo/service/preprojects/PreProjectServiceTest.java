package br.gov.es.openpmo.service.preprojects;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.gov.es.openpmo.dto.preprojects.CreatePreProjectRequest;
import br.gov.es.openpmo.dto.preprojects.PreProjectDto;
import br.gov.es.openpmo.dto.preprojects.SavePreProjectCriteriaTabValuesRequest;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectCriteriaListValueDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectCriteriaSelectionValueDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectListItemDto;
import br.gov.es.openpmo.enumerator.CriteriaOperation;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.preprojects.PreProject;
import br.gov.es.openpmo.model.preprojects.models.PreProjectModel;
import br.gov.es.openpmo.model.properties.CriteriaList;
import br.gov.es.openpmo.model.properties.CriteriaSelection;
import br.gov.es.openpmo.model.properties.CriteriaTab;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.SelectionOption;
import br.gov.es.openpmo.model.properties.models.CriteriaListModel;
import br.gov.es.openpmo.model.properties.models.CriteriaSelectionModel;
import br.gov.es.openpmo.model.properties.models.CriteriaTabModel;
import br.gov.es.openpmo.model.relations.Accepts;
import br.gov.es.openpmo.repository.PreProjectModelRepository;
import br.gov.es.openpmo.repository.PreProjectRepository;
import br.gov.es.openpmo.repository.PropertyRepository;
import br.gov.es.openpmo.service.actors.OrganizationService;
import br.gov.es.openpmo.service.properties.PropertyModelService;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class PreProjectServiceTest {

  private PreProjectRepository preProjectRepository;

  private PreProjectModelRepository preProjectModelRepository;

  private PropertyRepository propertyRepository;

  private PropertyModelService propertyModelService;

  private OrganizationService organizationService;

  private PreProjectService service;

  @Before
  public void setUp() {
    this.preProjectRepository = mock(PreProjectRepository.class);
    this.preProjectModelRepository = mock(PreProjectModelRepository.class);
    this.propertyRepository = mock(PropertyRepository.class);
    this.propertyModelService = mock(PropertyModelService.class);
    this.organizationService = mock(OrganizationService.class);
    this.service = new PreProjectService(
      this.preProjectRepository,
      this.preProjectModelRepository,
      this.propertyRepository,
      this.propertyModelService,
      this.organizationService,
      new InstantiatePreProjectProperty(),
      new PreProjectPropertyValueMapper()
    );
  }

  @Test
  public void shouldCreatePreProjectFromSavedModel() {
    final PreProjectModel preProjectModel = new PreProjectModel();
    preProjectModel.setId(10L);
    final CriteriaTabModel thinTabModel = new CriteriaTabModel();
    thinTabModel.setId(20L);
    preProjectModel.setProperties(Collections.singleton(thinTabModel));

    final CriteriaListModel listModel = new CriteriaListModel();
    listModel.setId(22L);
    listModel.setName("relevance-items");
    listModel.setSortIndex(1L);
    final CriteriaTabModel fullTabModel = new CriteriaTabModel();
    fullTabModel.setId(20L);
    fullTabModel.setName("relevance");
    fullTabModel.setSortIndex(1L);
    fullTabModel.setOperation(CriteriaOperation.AVERAGE);
    fullTabModel.setOrganizedProperties(Collections.singleton(listModel));

    final Organization organization = new Organization();
    organization.setId(30L);
    final CreatePreProjectRequest request = new CreatePreProjectRequest();
    request.setName("TVE Revista");
    request.setFullName("Producao e exibicao do programa TVE Revista");
    request.setIdOffice(2L);
    request.setIdOrganization(30L);

    when(this.preProjectModelRepository.findIdByOfficeId(2L)).thenReturn(Optional.of(10L));
    when(this.preProjectModelRepository.findById(10L)).thenReturn(Optional.of(preProjectModel));
    when(this.propertyModelService.findByIdWithChildren(20L)).thenReturn(fullTabModel);
    when(this.organizationService.findById(30L)).thenReturn(organization);
    when(this.preProjectRepository.save(any(PreProject.class))).thenAnswer(invocation -> {
      final PreProject saved = invocation.getArgument(0);
      saved.setId(40L);
      return saved;
    });

    final PreProjectDto result = this.service.create(request);

    assertEquals(Long.valueOf(40L), result.getId());
    assertEquals(Long.valueOf(10L), result.getIdPreProjectModel());
    assertEquals(Long.valueOf(30L), result.getIdOrganization());
    verify(this.preProjectRepository).save(org.mockito.ArgumentMatchers.argThat(preProject ->
      preProject.getInstance() == preProjectModel &&
        preProject.getOrganization() == organization &&
        preProject.getProperties().iterator().next() instanceof CriteriaTab &&
        preProject.getProperties().iterator().next().getPreProject() == preProject
    ));
  }

  @Test(expected = RegistroNaoEncontradoException.class)
  public void shouldNotCreatePreProjectWithoutAnOfficeModel() {
    final CreatePreProjectRequest request = new CreatePreProjectRequest();
    request.setIdOffice(2L);
    when(this.preProjectModelRepository.findIdByOfficeId(2L)).thenReturn(Optional.empty());

    this.service.create(request);
  }

  @Test
  public void shouldSaveCriteriaListAndSelectionValues() {
    final CriteriaListModel listModel = new CriteriaListModel();
    listModel.setId(101L);
    final CriteriaList list = new CriteriaList();
    list.setId(201L);
    list.setDriver(listModel);
    list.setValue(new HashSet<>());

    final SelectionOption option = new SelectionOption();
    option.setId(301L);
    option.setLabel("Alta");
    final CriteriaSelectionModel selectionModel = new CriteriaSelectionModel();
    selectionModel.setId(102L);
    final Accepts accepts = new Accepts();
    accepts.setCriteriaSelectionModel(selectionModel);
    accepts.setSelectionOption(option);
    selectionModel.setAcceptedOptions(Collections.singleton(accepts));
    final CriteriaSelection selection = new CriteriaSelection();
    selection.setId(202L);
    selection.setDriver(selectionModel);
    selection.setValue(new HashSet<>());

    final CriteriaTab tab = new CriteriaTab();
    tab.setId(200L);
    final CriteriaTabModel tabModel = new CriteriaTabModel();
    tabModel.setId(100L);
    tab.setDriver(tabModel);
    tab.setValue(new HashSet<>(Arrays.asList(list, selection)));
    final PreProjectListItemDto item = new PreProjectListItemDto();
    item.setForeignKey("delivery-1");
    item.setLabel("Exibicoes diarias do jornal");
    final PreProjectCriteriaListValueDto listValue = new PreProjectCriteriaListValueDto();
    listValue.setId(201L);
    listValue.setIdPropertyModel(101L);
    final PreProjectListItemDto secondItem = new PreProjectListItemDto();
    secondItem.setForeignKey("delivery-2");
    secondItem.setLabel("Publicacao semanal");
    listValue.setItems(Arrays.asList(item, secondItem));
    final PreProjectCriteriaSelectionValueDto selectionValue =
      new PreProjectCriteriaSelectionValueDto();
    selectionValue.setId(202L);
    selectionValue.setIdPropertyModel(102L);
    selectionValue.setSelectedOptionIds(Collections.singletonList(301L));
    final SavePreProjectCriteriaTabValuesRequest request =
      new SavePreProjectCriteriaTabValuesRequest();
    request.setValues(Arrays.asList(listValue, selectionValue));

    when(this.preProjectRepository.findCriteriaTabByModelId(50L, 100L))
      .thenReturn(Optional.of(tab));
    this.service.saveCriteriaTabValues(50L, 100L, request);

    assertEquals(2, list.getValue().size());
    assertEquals(Collections.singleton(option), selection.getValue());
    verify(this.propertyRepository).deleteCriteriaListItems(201L);
    verify(this.propertyRepository).deleteCriteriaSelectionValues(202L);
    verify(this.propertyRepository).saveAll(org.mockito.ArgumentMatchers.argThat(properties -> {
      final HashSet<Property> saved = new HashSet<>();
      properties.forEach(saved::add);
      return saved.contains(list) && saved.contains(selection);
    }));
  }

  @Test(expected = RegistroNaoEncontradoException.class)
  public void shouldNotSaveAPropertyFromAnotherCriteriaTab() {
    final CriteriaTab tab = new CriteriaTab();
    tab.setId(200L);
    final CriteriaTabModel tabModel = new CriteriaTabModel();
    tabModel.setId(100L);
    tab.setDriver(tabModel);
    tab.setValue(Collections.emptySet());
    final PreProjectCriteriaListValueDto foreignValue = new PreProjectCriteriaListValueDto();
    foreignValue.setId(999L);
    foreignValue.setIdPropertyModel(101L);
    foreignValue.setItems(Collections.emptyList());
    final SavePreProjectCriteriaTabValuesRequest request =
      new SavePreProjectCriteriaTabValuesRequest();
    request.setValues(Collections.singletonList(foreignValue));
    when(this.preProjectRepository.findCriteriaTabByModelId(50L, 100L))
      .thenReturn(Optional.of(tab));

    this.service.saveCriteriaTabValues(50L, 100L, request);
  }

}
