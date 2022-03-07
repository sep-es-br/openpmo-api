package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataResponse;
import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import br.gov.es.openpmo.service.dashboards.v2.IDashboardMilestoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Test build of dashboard data")
class GetDashboardDataTest {

  private static final Long ID_WORKPACK = 1L;
  private GetDashboardData getDashboardData;
  @Mock
  private IGetRiskDashboardData getRiskDashboardData;
  @Mock
  private IDashboardMilestoneService getMilestoneDashboardData;
  @Mock
  private IGetTripleConstraintData getTripleConstraintData;
  @Mock
  private IGetDatasheetDashboardData getDatasheetDashboardData;
  @Mock
  private IGetEarnedValueAnalysisData getEarnedValueAnalysisData;
  @Mock
  private DashboardParameters parameters;
  @Mock
  private RiskDataChart riskDataChartMock;
  @Mock
  private MilestoneDataChart milestoneDataChartMock;
  @Mock
  private TripleConstraintDataChart tripleConstraintDataChartMock;
  @Mock
  private DatasheetResponse datasheetMock;
  @Mock
  private DashboardEarnedValueAnalysis dashboardEarnedValueMock;

  @BeforeEach
  void setUp() {
    this.getDashboardData = new GetDashboardData(
            this.getRiskDashboardData,
            this.getMilestoneDashboardData,
            this.getTripleConstraintData,
            this.getDatasheetDashboardData,
            this.getEarnedValueAnalysisData
    );
  }

  @Test
  @DisplayName("Should build datasheet data when 'showHeader' true")
  void test1() {
    doReturn(this.riskDataChartMock).when(this.getRiskDashboardData).get(ID_WORKPACK);
    doReturn(this.milestoneDataChartMock).when(this.getMilestoneDashboardData).build(this.parameters);
    doReturn(this.tripleConstraintDataChartMock).when(this.getTripleConstraintData).get(this.parameters);
    doReturn(this.datasheetMock).when(this.getDatasheetDashboardData).get(this.parameters);
    doReturn(this.dashboardEarnedValueMock).when(this.getEarnedValueAnalysisData).get(this.parameters);

    doReturn(ID_WORKPACK).when(this.parameters).getWorkpackId();
    doReturn(true).when(this.parameters).getShowHeader();

    final DashboardDataResponse dashboardDataResponse = this.getDashboardData.get(this.parameters);

    assertNotNull(dashboardDataResponse.getDatasheet(), "Datasheet data should be not null");
    verify(this.getRiskDashboardData, times(1)).get(ID_WORKPACK);
    verify(this.getMilestoneDashboardData, times(1)).build(this.parameters);
    verify(this.getTripleConstraintData, times(1)).get(this.parameters);
    verify(this.getDatasheetDashboardData, times(1)).get(this.parameters);
    verify(this.getEarnedValueAnalysisData, times(1)).get(this.parameters);
    verify(this.parameters, times(1)).getWorkpackId();
    verify(this.parameters, times(1)).getShowHeader();
  }

  @Test
  @DisplayName("Should not build datasheet data when 'showHeader' false")
  void test2() {

    doReturn(this.riskDataChartMock).when(this.getRiskDashboardData).get(ID_WORKPACK);
    doReturn(this.milestoneDataChartMock).when(this.getMilestoneDashboardData).build(this.parameters);
    doReturn(this.tripleConstraintDataChartMock).when(this.getTripleConstraintData).get(this.parameters);
    doReturn(this.dashboardEarnedValueMock).when(this.getEarnedValueAnalysisData).get(this.parameters);

    doReturn(ID_WORKPACK).when(this.parameters).getWorkpackId();
    doReturn(false).when(this.parameters).getShowHeader();

    final DashboardDataResponse dashboardDataResponse = this.getDashboardData.get(this.parameters);

    assertNull(dashboardDataResponse.getDatasheet(), "Datasheet data should be not null");
    verify(this.getRiskDashboardData, times(1)).get(ID_WORKPACK);
    verify(this.getMilestoneDashboardData, times(1)).build(this.parameters);
    verify(this.getTripleConstraintData, times(1)).get(this.parameters);
    verify(this.getDatasheetDashboardData, never()).get(this.parameters);
    verify(this.getEarnedValueAnalysisData, times(1)).get(this.parameters);
    verify(this.parameters, times(1)).getWorkpackId();
    verify(this.parameters, times(1)).getShowHeader();
  }

}
