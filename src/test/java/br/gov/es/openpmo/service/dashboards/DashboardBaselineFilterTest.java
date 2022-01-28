package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.repository.dashboards.DashboardBaselineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Test if baseline is filtering correctly for dashboard")
class DashboardBaselineFilterTest {

  DashboardBaselineFilter underTest;

  @Mock
  DashboardBaselineRepository baselineRepository;

  @BeforeEach
  void setUp() {
    this.underTest = new DashboardBaselineFilter(this.baselineRepository);
  }

  @Test
  @DisplayName("Should call repository method once")
  void test1() {
    this.underTest.getBaselines(1L);
    verify(this.baselineRepository, times(1)).findAllByWorkpackId(1L);
  }

}