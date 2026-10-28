package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.properties.models.IntegerModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;

public class GetPropertyModelDtosFromEntitiesTest {

  @Test
  public void sortsIndexedPropertiesFirstAndKeepsMissingSortIndexLast() {
    final GetPropertyModelDtoFromEntity converter = mock(GetPropertyModelDtoFromEntity.class);
    final GetPropertyModelDtosFromEntities service = new GetPropertyModelDtosFromEntities(converter);
    final PropertyModel unindexedProperty = new IntegerModel();
    final PropertyModel secondProperty = new IntegerModel();
    final PropertyModel firstProperty = new IntegerModel();
    final PropertyModel unsupportedProperty = new IntegerModel();
    final PropertyModelDto unindexedDto = dto(3L, null);
    final PropertyModelDto secondDto = dto(2L, 2L);
    final PropertyModelDto firstDto = dto(1L, 1L);

    when(converter.execute(same(unindexedProperty))).thenReturn(unindexedDto);
    when(converter.execute(same(secondProperty))).thenReturn(secondDto);
    when(converter.execute(same(firstProperty))).thenReturn(firstDto);
    when(converter.execute(same(unsupportedProperty))).thenReturn(null);

    final List<? extends PropertyModelDto> result = service.execute(
      Arrays.asList(unindexedProperty, secondProperty, firstProperty, unsupportedProperty)
    );

    assertEquals(Arrays.asList(1L, 2L, 3L), Arrays.asList(
      result.get(0).getId(), result.get(1).getId(), result.get(2).getId()
    ));
  }

  private static PropertyModelDto dto(final Long id, final Long sortIndex) {
    final PropertyModelDto dto = new PropertyModelDto();
    dto.setId(id);
    dto.setSortIndex(sortIndex);
    return dto;
  }
}
