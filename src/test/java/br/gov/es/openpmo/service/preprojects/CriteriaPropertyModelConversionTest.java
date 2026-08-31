package br.gov.es.openpmo.service.preprojects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaListModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaTabModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.enumerator.CriteriaOperation;
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

    final CriteriaTabModel tabModel = new CriteriaTabModel();
    tabModel.setSortIndex(1L);
    tabModel.setName("criteria");
    tabModel.setLabel("Criterios");
    tabModel.setWeight(2D);
    tabModel.setOperation(CriteriaOperation.AVERAGE);
    tabModel.setOrganizedProperties(Collections.singleton(listModel));

    final PropertyModelDto result = new GetPropertyModelDtoFromEntity().execute(tabModel);

    assertTrue(result instanceof CriteriaTabModelDto);
    final CriteriaTabModelDto tabDto = (CriteriaTabModelDto) result;
    assertEquals(CriteriaOperation.AVERAGE, tabDto.getOperation());
    assertEquals(Double.valueOf(2D), tabDto.getWeight());
    assertTrue(tabDto.getOrganizedProperties().get(0) instanceof CriteriaListModelDto);
    final CriteriaListModelDto listDto = (CriteriaListModelDto) tabDto.getOrganizedProperties().get(0);
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

    final CriteriaTabModelDto tabDto = new CriteriaTabModelDto();
    tabDto.setSortIndex(1L);
    tabDto.setName("criteria");
    tabDto.setLabel("Criterios");
    tabDto.setWeight(2D);
    tabDto.setOperation(CriteriaOperation.SUM);
    tabDto.setOrganizedProperties(Collections.singletonList(listDto));

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
    assertTrue(tabModel.getOrganizedProperties().iterator().next() instanceof CriteriaListModel);
    final CriteriaListModel listModel = (CriteriaListModel) tabModel.getOrganizedProperties().iterator().next();
    assertEquals(Double.valueOf(3D), listModel.getWeight());
    assertEquals(Double.valueOf(5D), listModel.getItemValue());
  }

}
