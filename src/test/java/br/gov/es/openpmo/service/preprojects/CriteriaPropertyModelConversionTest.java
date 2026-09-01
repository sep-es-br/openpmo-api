package br.gov.es.openpmo.service.preprojects;

import static org.junit.Assert.assertEquals;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.modelmapper.ModelMapper;

public class CriteriaPropertyModelConversionTest {

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
    tabModel.setOrganizedProperties(Collections.singleton(groupModel));

    final PropertyModelDto result = new GetPropertyModelDtoFromEntity().execute(tabModel);

    assertTrue(result instanceof CriteriaTabModelDto);
    final CriteriaTabModelDto tabDto = (CriteriaTabModelDto) result;
    assertEquals(CriteriaOperation.AVERAGE, tabDto.getOperation());
    assertEquals(Double.valueOf(2D), tabDto.getWeight());
    assertEquals("settings", tabDto.getIcon());
    assertTrue(tabDto.getOrganizedProperties().get(0) instanceof CriteriaGroupModelDto);
    final CriteriaGroupModelDto groupDto = (CriteriaGroupModelDto) tabDto.getOrganizedProperties().get(0);
    assertTrue(groupDto.isEnablementKey());
    assertEquals(Double.valueOf(0D), groupDto.getDisabledValue());
    assertEquals("Legenda do grupo", groupDto.getLegend());
    final CriteriaListModelDto listDto = (CriteriaListModelDto) groupDto.getGroupedProperties().get(0);
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
    groupDto.setGroupedProperties(Collections.singletonList(listDto));

    final CriteriaTabModelDto tabDto = new CriteriaTabModelDto();
    tabDto.setSortIndex(1L);
    tabDto.setName("criteria");
    tabDto.setLabel("Criterios");
    tabDto.setIcon("settings");
    tabDto.setWeight(2D);
    tabDto.setOperation(CriteriaOperation.SUM);
    tabDto.setOrganizedProperties(Collections.singletonList(groupDto));

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
    assertTrue(tabModel.getOrganizedProperties().iterator().next() instanceof CriteriaGroupModel);
    final CriteriaGroupModel groupModel =
      (CriteriaGroupModel) tabModel.getOrganizedProperties().iterator().next();
    assertTrue(groupModel.isEnablementKey());
    assertEquals(Double.valueOf(0D), groupModel.getDisabledValue());
    assertEquals("Legenda do grupo", groupModel.getLegend());
    final CriteriaListModel listModel = (CriteriaListModel) groupModel.getGroupedProperties().iterator().next();
    assertEquals(Double.valueOf(3D), listModel.getWeight());
    assertEquals(Double.valueOf(5D), listModel.getItemValue());
  }

}
