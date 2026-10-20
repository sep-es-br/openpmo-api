package br.gov.es.openpmo.controller.search;

import br.gov.es.openpmo.dto.person.queries.PersonQuery;
import br.gov.es.openpmo.dto.universalSearch.UniversalSearchParameters;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization;
import br.gov.es.openpmo.service.workpack.GetTransversalSearchWorkpackIds;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import br.gov.es.openpmo.utils.ResponseHandler;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SearchControllerTest {

  @Test
  public void searchesUsingTheResolvedPersonRatherThanTheTokenSubject() {
    final WorkpackService workpackService = mock(WorkpackService.class);
    final IGetPersonFromAuthorization personResolver = mock(IGetPersonFromAuthorization.class);
    final GetTransversalSearchWorkpackIds transversalSearchWorkpackIds = mock(GetTransversalSearchWorkpackIds.class);
    final ResponseHandler responseHandler = mock(ResponseHandler.class);
    final IGetPersonFromAuthorization.PersonDataResponse personData = mock(PersonQuery.class);
    final Person person = mock(Person.class);
    when(person.getId()).thenReturn(1086113L);
    when(personData.getPerson()).thenReturn(person);
    when(personData.getKey()).thenReturn("renata-person-key");
    when(personResolver.execute(anyString())).thenReturn(personData);
    when(transversalSearchWorkpackIds.findAccessibleWorkpackProgramIds(182L, "renata-person-key", "Bearer token"))
      .thenReturn(java.util.Collections.singletonMap(33073L, java.util.Collections.singletonList(44L)));
    when(workpackService.doUniversalSearch(any(UniversalSearchParameters.class), any(PageRequest.class)))
      .thenReturn(Page.empty());

    final SearchController controller = new SearchController(
      workpackService,
      personResolver,
      transversalSearchWorkpackIds,
      responseHandler
    );
    controller.getDashboard(182L, null, "Academia", 0, 30, "Bearer token");

    verify(personResolver).execute("Bearer token");
    verify(workpackService).doUniversalSearch(
      org.mockito.ArgumentMatchers.argThat(parameters -> parameters.getUserId().equals(1086113L)
        && parameters.getUserKey().equals("renata-person-key")
        && parameters.getTransversalWorkpackIds().contains(33073L)),
      eq(PageRequest.of(0, 30))
    );
  }
}
