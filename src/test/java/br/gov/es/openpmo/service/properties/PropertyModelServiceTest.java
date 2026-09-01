package br.gov.es.openpmo.service.properties;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.gov.es.openpmo.model.properties.models.CriteriaTabModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import java.util.Optional;
import org.junit.Test;

public class PropertyModelServiceTest {

  @Test
  public void shouldFindPropertyModelWithAllChildren() {
    final PropertyModelRepository repository = mock(PropertyModelRepository.class);
    final PropertyModelService service = new PropertyModelService(repository);
    final CriteriaTabModel criteriaTabModel = new CriteriaTabModel();
    criteriaTabModel.setId(30L);
    when(repository.findByIdWithChildren(30L)).thenReturn(Optional.of(criteriaTabModel));

    final PropertyModel result = service.findByIdWithChildren(30L);

    assertSame(criteriaTabModel, result);
    verify(repository).findByIdWithChildren(30L);
  }

}
