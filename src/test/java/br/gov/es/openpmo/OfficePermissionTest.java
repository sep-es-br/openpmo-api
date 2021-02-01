package br.gov.es.openpmo;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.gov.es.openpmo.controller.OfficeController;
import br.gov.es.openpmo.controller.OfficePermissionController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionDto;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionParamDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.Entity;

@Testcontainers
@SpringBootTest
public class OfficePermissionTest extends BaseTest {

    @Autowired
    private OfficePermissionController officePermissionController;

    @Autowired
    private OfficeController officeController;

    private Long idOffice;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void loadOffice() {
        if (this.idOffice == null) {
            OfficeStoreDto office = new OfficeStoreDto();
            office.setName("Office Test OfficePermission");
            office.setFullName("Office Test OfficePermission ");
            ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idOffice = response.getBody().getData().getId();
        }
    }

    @Test
    public void shouldCreateOfficePermission() {
        OfficePermissionParamDto officePermission = new OfficePermissionParamDto();
        officePermission.setEmail("office.test@openpmo.com");
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setLevel(PermissionLevelEnum.EDIT);
        permissionDto.setRole("roleTest");
        officePermission.setPermissions(new ArrayList<>());
        officePermission.getPermissions().add(permissionDto);
        officePermission.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<Entity>> response = officePermissionController.store(officePermission);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    public void shouldUpdateOfficePermission() {
        OfficePermissionParamDto officePermission = new OfficePermissionParamDto();
        officePermission.setEmail("office.test@openpmo.com");
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setLevel(PermissionLevelEnum.EDIT);
        permissionDto.setRole("roleTest");
        officePermission.setPermissions(new ArrayList<>());
        officePermission.getPermissions().add(permissionDto);
        officePermission.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<Entity>> response = officePermissionController.store(officePermission);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        OfficePermissionParamDto officePermissionUpdate = new OfficePermissionParamDto();
        officePermissionUpdate.setEmail("office.test@openpmo.com");
        officePermissionUpdate.setIdOffice(idOffice);
        response = officePermissionController.update(officePermissionUpdate);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    public void shouldDelete() {
        OfficePermissionParamDto officePermission = new OfficePermissionParamDto();
        officePermission.setEmail("office.test@openpmo.com");
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setLevel(PermissionLevelEnum.EDIT);
        permissionDto.setRole("roleTest");
        officePermission.setPermissions(new ArrayList<>());
        officePermission.getPermissions().add(permissionDto);
        officePermission.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<Entity>> response = officePermissionController.store(officePermission);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        ResponseEntity<Void> responseDelete = officePermissionController.delete(idOffice, "office.test@openpmo.com");
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
    }

    @Test
    public void shouldListAll() {
        OfficePermissionParamDto officePermission = new OfficePermissionParamDto();
        officePermission.setEmail("office.test@openpmo.com");
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setLevel(PermissionLevelEnum.EDIT);
        permissionDto.setRole("roleTest");
        officePermission.setPermissions(new ArrayList<>());
        officePermission.getPermissions().add(permissionDto);
        officePermission.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<Entity>> response = officePermissionController.store(officePermission);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        ResponseEntity<ResponseBase<List<OfficePermissionDto>>> responseList = officePermissionController.indexBase(idOffice, null);
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
        Assertions.assertFalse(responseList.getBody().getData().isEmpty());
    }

}
