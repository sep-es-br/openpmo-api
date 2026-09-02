package br.gov.es.openpmo.service.preprojects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaGroupModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaListModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaTabModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.enumerator.CriteriaOperation;
import br.gov.es.openpmo.model.properties.models.CriteriaGroupModel;
import br.gov.es.openpmo.model.properties.models.CriteriaListModel;
import br.gov.es.openpmo.model.properties.models.CriteriaTabModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.service.actors.OrganizationService;
import br.gov.es.openpmo.service.office.DomainService;
import br.gov.es.openpmo.service.office.LocalityService;
import br.gov.es.openpmo.service.office.UnitMeasureService;
import br.gov.es.openpmo.service.reports.models.ExtractPropertyModel;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelDtoFromEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.modelmapper.ModelMapper;

public class CriteriaPropertyModelConversionTest {

  @Test
  public void shouldExposeOnlyCriteriaCollectionNamesInJson() throws Exception {
    final CriteriaGroupModelDto groupDto = new CriteriaGroupModelDto();
    groupDto.setProperties(Collections.emptyList());
    final CriteriaListModelDto propertyDto = new CriteriaListModelDto();

    final CriteriaTabModelDto tabDto = new CriteriaTabModelDto();
    tabDto.setOrganized(Collections.singletonList(groupDto));
    tabDto.setProperties(Collections.singletonList(propertyDto));

    final ObjectMapper objectMapper = new ObjectMapper();
    final String json = objectMapper.writeValueAsString(tabDto);
    final JsonNode jsonNode = objectMapper.readTree(json);

    assertTrue(json.contains("\"organized\":"));
    assertTrue(json.contains("\"properties\":"));
    assertFalse(json.contains("\"organizedProperties\":"));
    assertFalse(json.contains("\"groupedProperties\":"));
    assertEquals("CriteriaGroupModel", jsonNode.get("organized").get(0).get("type").asText());
    assertEquals("CriteriaListModel", jsonNode.get("properties").get(0).get("type").asText());
  }

  @Test
  public void shouldConvertNestedCriteriaFromEntityToDto() {
    final CriteriaListModel listModel = new CriteriaListModel();
    listModel.setSortIndex(2L);
    listModel.setName("score");
    listModel.setLabel("Pontuacao");
    listModel.setWeight(3D);
    listModel.setItemValue(5D);

    final CriteriaGroupModel groupModel = new CriteriaGroupModel();
    groupModel.setSortIndex(1L);
    groupModel.setName("strategic-alignment");
    groupModel.setLabel("Alinhamento Estrategico");
    groupModel.setWeight(1D);
    groupModel.setOperation(CriteriaOperation.SUM);
    groupModel.setEnablementKey(true);
    groupModel.setDisabledValue(0D);
    groupModel.setLegend("Legenda do grupo");
    groupModel.setGroupedProperties(Collections.singleton(listModel));

    final CriteriaTabModel tabModel = new CriteriaTabModel();
    tabModel.setSortIndex(1L);
    tabModel.setName("criteria");
    tabModel.setLabel("Criterios");
    tabModel.setIcon("settings");
    tabModel.setWeight(2D);
    tabModel.setOperation(CriteriaOperation.AVERAGE);
    final CriteriaListModel standaloneListModel = new CriteriaListModel();
    standaloneListModel.setSortIndex(3L);
    standaloneListModel.setName("cost");
    standaloneListModel.setLabel("Custo");
    standaloneListModel.setWeight(2D);
    standaloneListModel.setItemValue(10D);
    tabModel.setOrganizedProperties(new HashSet<>(java.util.Arrays.asList(groupModel, standaloneListModel)));

    final PropertyModelDto result = new GetPropertyModelDtoFromEntity().execute(tabModel);

    assertTrue(result instanceof CriteriaTabModelDto);
    final CriteriaTabModelDto tabDto = (CriteriaTabModelDto) result;
    assertEquals(CriteriaOperation.AVERAGE, tabDto.getOperation());
    assertEquals(Double.valueOf(2D), tabDto.getWeight());
    assertEquals("settings", tabDto.getIcon());
    assertEquals(1, tabDto.getOrganized().size());
    assertEquals(1, tabDto.getProperties().size());
    assertTrue(tabDto.getOrganized().get(0) instanceof CriteriaGroupModelDto);
    assertTrue(tabDto.getProperties().get(0) instanceof CriteriaListModelDto);
    final CriteriaGroupModelDto groupDto = (CriteriaGroupModelDto) tabDto.getOrganized().get(0);
    assertTrue(groupDto.isEnablementKey());
    assertEquals(Double.valueOf(0D), groupDto.getDisabledValue());
    assertEquals("Legenda do grupo", groupDto.getLegend());
    final CriteriaListModelDto listDto = (CriteriaListModelDto) groupDto.getProperties().get(0);
    assertEquals(Double.valueOf(3D), listDto.getWeight());
    assertEquals(Double.valueOf(5D), listDto.getItemValue());
  }

  @Test
  public void shouldConvertNestedCriteriaFromDtoToEntity() {
    final CriteriaListModelDto listDto = new CriteriaListModelDto();
    listDto.setSortIndex(2L);
    listDto.setName("score");
    listDto.setLabel("Pontuacao");
    listDto.setWeight(3D);
    listDto.setItemValue(5D);

    final CriteriaGroupModelDto groupDto = new CriteriaGroupModelDto();
    groupDto.setSortIndex(1L);
    groupDto.setName("strategic-alignment");
    groupDto.setLabel("Alinhamento Estrategico");
    groupDto.setWeight(1D);
    groupDto.setOperation(CriteriaOperation.SUM);
    groupDto.setEnablementKey(true);
    groupDto.setDisabledValue(0D);
    groupDto.setLegend("Legenda do grupo");
    groupDto.setProperties(Collections.singletonList(listDto));

    final CriteriaTabModelDto tabDto = new CriteriaTabModelDto();
    tabDto.setSortIndex(1L);
    tabDto.setName("criteria");
    tabDto.setLabel("Criterios");
    tabDto.setIcon("settings");
    tabDto.setWeight(2D);
    tabDto.setOperation(CriteriaOperation.SUM);
    tabDto.setOrganized(Collections.singletonList(groupDto));
    final CriteriaListModelDto standaloneListDto = new CriteriaListModelDto();
    standaloneListDto.setSortIndex(3L);
    standaloneListDto.setName("cost");
    standaloneListDto.setLabel("Custo");
    standaloneListDto.setWeight(2D);
    standaloneListDto.setItemValue(10D);
    tabDto.setProperties(Collections.singletonList(standaloneListDto));

    final ExtractPropertyModel converter = new ExtractPropertyModel(
      new ModelMapper(),
      mock(UnitMeasureService.class),
      mock(DomainService.class),
      mock(LocalityService.class),
      mock(OrganizationService.class)
    );
    final Set<PropertyModel> properties = new HashSet<>();

    converter.execute(properties, tabDto);

    assertEquals(1, properties.size());
    final CriteriaTabModel tabModel = (CriteriaTabModel) properties.iterator().next();
    assertEquals(CriteriaOperation.SUM, tabModel.getOperation());
    assertEquals(Double.valueOf(2D), tabModel.getWeight());
    assertEquals("settings", tabModel.getIcon());
    assertEquals(2, tabModel.getOrganizedProperties().size());
    assertTrue(tabModel.getOrganizedProperties().stream().anyMatch(CriteriaGroupModel.class::isInstance));
    assertTrue(tabModel.getOrganizedProperties().stream().anyMatch(CriteriaListModel.class::isInstance));
    final CriteriaGroupModel groupModel =
      (CriteriaGroupModel) tabModel.getOrganizedProperties().stream()
        .filter(CriteriaGroupModel.class::isInstance)
        .findFirst()
        .orElseThrow(AssertionError::new);
    assertTrue(groupModel.isEnablementKey());
    assertEquals(Double.valueOf(0D), groupModel.getDisabledValue());
    assertEquals("Legenda do grupo", groupModel.getLegend());
    final CriteriaListModel listModel = (CriteriaListModel) groupModel.getGroupedProperties().iterator().next();
    assertEquals(Double.valueOf(3D), listModel.getWeight());
    assertEquals(Double.valueOf(5D), listModel.getItemValue());
  }

}
