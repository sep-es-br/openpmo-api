package br.gov.es.openpmo.dto.person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import br.gov.es.openpmo.dto.officepermission.OfficePermissionParamDto;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionParamDto;
import br.gov.es.openpmo.dto.ccbmembers.CCBMemberRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.Test;

public class PersonDtoJsonTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void officePermissionIgnoresPersonRolesAndKeepsCcmPermission() throws Exception {
    final OfficePermissionParamDto request = this.mapper.readValue(
      payload("idOffice"), OfficePermissionParamDto.class);

    assertEquals(Long.valueOf(10), request.getPerson().getId());
    assertEquals("citizen", request.getPerson().getRoles().get(0).getRole());
    assertEquals(1, request.getPerson().getRoles().size());
    assertTrue(request.getPermissions().get(0).isCcmMember());
  }

  @Test
  public void planPermissionIgnoresPersonRolesAndKeepsCcmPermission() throws Exception {
    final PlanPermissionParamDto request = this.mapper.readValue(
      payload("idPlan"), PlanPermissionParamDto.class);

    assertEquals(Long.valueOf(10), request.getPerson().getId());
    assertEquals("citizen", request.getPerson().getRoles().get(0).getRole());
    assertEquals(1, request.getPerson().getRoles().size());
    assertTrue(request.getPermissions().get(0).isCcmMember());
  }

  @Test
  public void personRolesRemainAvailableInResponses() throws Exception {
    final PersonDto person = new PersonDto();
    person.addRole(new RoleResource("Manager", "Office"));

    assertEquals("Manager", person.getRoles().get(1).getRole());
    assertTrue(this.mapper.writeValueAsString(person).contains("\"role\":\"Manager\""));
  }

  @Test
  public void projectCcmRequestAlsoIgnoresPersonRoles() throws Exception {
    final CCBMemberRequest request = this.mapper.readValue(
      "{\"idWorkpack\":1,\"idOffice\":2,\"person\":{\"id\":10," +
        "\"roles\":[{\"role\":\"Manager\"}]}}",
      CCBMemberRequest.class);

    assertEquals(Long.valueOf(10), request.getPerson().getId());
    assertEquals(1, request.getPerson().getRoles().size());
  }

  @Test
  public void personBuilderCanAddRolesWithoutMutatingGetterResult() {
    final PersonDto person = PersonDtoBuilder.aPersonDto()
      .withRoles(Collections.singletonList(new RoleResource("Manager", "Office")))
      .build();

    assertEquals("Manager", person.getRoles().get(1).getRole());
  }

  private static String payload(final String targetIdField) {
    return "{\"" + targetIdField + "\":10,\"key\":\"person@example.com\"," +
      "\"person\":{\"id\":10,\"name\":\"Person\",\"roles\":[{\"role\":\"Manager\"}]}," +
      "\"permissions\":[{\"role\":\"Manager\",\"level\":\"EDIT\",\"ccmMember\":true}]}";
  }
}
