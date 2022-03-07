package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetTotalizers;
import br.gov.es.openpmo.repository.dashboards.DashboardDatasheetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Test if dashboard datasheet is being retrieved correctly")
class GetDatasheetDashboardDataTest {

  @Mock
  DashboardDatasheetRepository repository;

  @Mock
  UriComponentsBuilder uriComponentsBuilder;

  GetDatasheetDashboardData underTest;

  SecureRandom secureRandom;

  @BeforeEach
  void setUp() {
    this.underTest = new GetDatasheetDashboardData(this.repository);
    this.secureRandom = new SecureRandom();
  }

  @Test
  @DisplayName("Should retrived the totalizers correctly")
  void test1() {
    // given
    final Long qtyProjects = this.getLongRandom();
    final Long qtyDeliverables = this.getLongRandom();
    final Long qtyMilestones = this.getLongRandom();
    final Long workpackId = this.getLongRandom();

    doReturn(qtyProjects).when(this.repository).quantityOfProjects(workpackId);
    doReturn(qtyDeliverables).when(this.repository).quantityOfDeliverables(workpackId);
    doReturn(qtyMilestones).when(this.repository).quantityOfMilestones(workpackId);

    // when
    final DatasheetResponse datasheet = this.underTest.get(new DashboardParameters(
            true,
            workpackId,
            null,
            null,
            this.uriComponentsBuilder
    ));
    final DatasheetTotalizers totalizers = datasheet.getDatasheetTotalizers();

    // then
    verify(this.repository, times(1)).quantityOfProjects(workpackId);
    verify(this.repository, times(1)).quantityOfDeliverables(workpackId);
    verify(this.repository, times(1)).quantityOfMilestones(workpackId);

    assertSame(qtyProjects, totalizers.getProjectsQuantity(), String.format("Quantity of Projects should be equal to %d", qtyProjects));
    assertSame(qtyDeliverables, totalizers.getDeliverablesQuantity(), String.format("Quantity of Deliverables should be equal to %d", qtyDeliverables));
    assertSame(qtyMilestones, totalizers.getMilestoneQuantity(), String.format("Quantity of Milestones should be equal to %d", qtyMilestones));
  }

  private long getLongRandom() {
    return this.secureRandom.nextLong();
  }

}
