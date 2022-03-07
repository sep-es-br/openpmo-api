package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.repository.dashboards.DashboardMilestoneRepository;
import br.gov.es.openpmo.service.dashboards.v2.DashboardMilestoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Test if Dashboard Milestone Data is being correctly returned")
class MilestoneServiceTest {

    @Mock
    DashboardMilestoneRepository repository;

    @Mock
    UriComponentsBuilder uriComponentsBuilder;

    private DashboardMilestoneService underTest;

    @BeforeEach
    void setUp() {
        this.underTest = new DashboardMilestoneService(this.repository);
    }

    @Test
    @DisplayName("Should call the methods that use baseline and return the expected values")
    void test1() {
        // given
        final Long quantity = 1L;
        final Long concluded = 2L;
        final Long lateConcluded = 3L;
        final Long late = 4L;
        final Long onTime = 5L;
        final YearMonth yearMonth = YearMonth.now();
        final LocalDate localDate = LocalDate.now();

        doReturn(quantity).when(this.repository).quantity(1L, 1L);
        doReturn(concluded).when(this.repository).concluded(1L, 1L);
        doReturn(lateConcluded).when(this.repository).lateConcluded(1L, 1L);
        doReturn(late).when(this.repository).late(1L, 1L, localDate);
        doReturn(onTime).when(this.repository).onTime(1L, 1L, localDate);

        lenient().doReturn(0L).when(this.repository).quantity(anyLong());
        lenient().doReturn(0L).when(this.repository).concluded(anyLong());
        lenient().doReturn(0L).when(this.repository).late(anyLong(), any(LocalDate.class));
        lenient().doReturn(0L).when(this.repository).onTime(anyLong(), any(LocalDate.class));

        // when
        final MilestoneDataChart milestone = this.underTest.build(new DashboardParameters(
                true,
                1L,
                1L,
                yearMonth,
                this.uriComponentsBuilder
        ));

        // then
        verify(this.repository, times(1)).quantity(1L, 1L);
        verify(this.repository, times(1)).concluded(1L, 1L);
        verify(this.repository, times(1)).lateConcluded(1L, 1L);
        verify(this.repository, times(1)).late(1L, 1L, localDate);
        verify(this.repository, times(1)).onTime(1L, 1L, localDate);

        verify(this.repository, never()).quantity(anyLong());
        verify(this.repository, never()).concluded(anyLong());
        verify(this.repository, never()).late(anyLong(), any(LocalDate.class));
        verify(this.repository, never()).onTime(anyLong(), any(LocalDate.class));

        assertSame(quantity, milestone.getQuantity(), String.format("Quantity should be equal to %d", quantity));
        assertSame(concluded, milestone.getConcluded(), String.format("Concluded should be equal to %d", concluded));
        assertSame(lateConcluded, milestone.getLateConcluded(), String.format("Late concluded should be equal to %d", lateConcluded));
        assertSame(late, milestone.getLate(), String.format("Late should be equal to %d", late));
        assertSame(onTime, milestone.getOnTime(), String.format("On time should be equal to %d", onTime));
    }

    @Test
    @DisplayName("Should call the methods that do not use baseline and return the expected values")
    void test2() {
        // given
        final Long quantity = 1L;
        final Long concluded = 2L;
        final Long late = 3L;
        final Long onTime = 4L;
        final YearMonth yearMonth = YearMonth.now();
        final LocalDate localDate = LocalDate.now();

        doReturn(quantity).when(this.repository).quantity(1L);
        doReturn(concluded).when(this.repository).concluded(1L);
        doReturn(late).when(this.repository).late(1L, localDate);
        doReturn(onTime).when(this.repository).onTime(1L, localDate);

        lenient().doReturn(0L).when(this.repository).quantity(nullable(Long.class), anyLong());
        lenient().doReturn(0L).when(this.repository).concluded(nullable(Long.class), anyLong());
        lenient().doReturn(0L).when(this.repository).lateConcluded(nullable(Long.class), anyLong());
        lenient().doReturn(0L).when(this.repository).late(nullable(Long.class), anyLong(), any(LocalDate.class));
        lenient().doReturn(0L).when(this.repository).onTime(nullable(Long.class), anyLong(), any(LocalDate.class));

        // when
        final MilestoneDataChart milestone =
                this.underTest.build(new DashboardParameters(true, 1L, null, yearMonth, this.uriComponentsBuilder));

        // then
        verify(this.repository, never()).quantity(anyLong(), anyLong());
        verify(this.repository, never()).concluded(anyLong(), anyLong());
        verify(this.repository, never()).lateConcluded(anyLong(), anyLong());
        verify(this.repository, never()).late(anyLong(), anyLong(), any(LocalDate.class));
        verify(this.repository, never()).onTime(anyLong(), anyLong(), any(LocalDate.class));

        verify(this.repository, times(1)).quantity(1L);
        verify(this.repository, times(1)).concluded(1L);
        verify(this.repository, times(1)).late(1L, localDate);
        verify(this.repository, times(1)).onTime(1L, localDate);

        assertSame(quantity, milestone.getQuantity(), String.format("Quantity should be equal to %d", quantity));
        assertSame(concluded, milestone.getConcluded(), String.format("Concluded should be equal to %d", concluded));
        assertSame(null, milestone.getLateConcluded(), "Late concluded should be equal to null");
        assertSame(late, milestone.getLate(), String.format("Late should be equal to %d", late));
        assertSame(onTime, milestone.getOnTime(), String.format("On time should be equal to %d", onTime));
    }

}
