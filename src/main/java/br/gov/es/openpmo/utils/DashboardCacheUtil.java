package br.gov.es.openpmo.utils;

import br.gov.es.openpmo.dto.dashboards.DashboardBaseline;
import br.gov.es.openpmo.dto.dashboards.DashboardDto;
import br.gov.es.openpmo.dto.dashboards.DashboardMonthDto;
import br.gov.es.openpmo.dto.dashboards.DashboardWorkpackDetailDto;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class DashboardCacheUtil {

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<WorkpackKey, DashboardWorkpackDetailDto> mapWorkpackDetail = new HashMap<>(0);
    private Map<Long, DashboardBaseline> mapDashboardBaseline = new HashMap<>(0);
    private boolean loadingAll;

    class WorkpackKey {
        private Long idWorkpack;
        private Long idPlan;

        public WorkpackKey(Long idWorkpack, Long idPlan) {
            this.idWorkpack = idWorkpack;
            this.idPlan = idPlan;
        }

        public Long getIdWorkpack() {
            return idWorkpack;
        }

        public Long getIdPlan() {
            return idPlan;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            WorkpackKey that = (WorkpackKey) o;
            return Objects.equals(idWorkpack, that.idWorkpack) && Objects.equals(idPlan, that.idPlan);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idWorkpack, idPlan);
        }

        @Override
        public String toString() {
            return "WorkpackKey{" + "idWorkpack=" + idWorkpack + ", idPlan=" + idPlan + '}';
        }
    }

    private List<DashboardWorkpackDetailDto> getMilestones(DashboardRepository dashboardRepository
        , final List<Long> balineIds, List<Long> workpackIds, Long idPlan) {
        final List<DashboardWorkpackDetailDto> milestoneDetail = dashboardRepository.findAllMilestoneMaster(workpackIds, idPlan);
        final List<DashboardWorkpackDetailDto> milestoneBaseline = dashboardRepository.findAllMilestoneBaseline(balineIds, workpackIds, idPlan);
        milestoneBaseline.forEach(b -> milestoneDetail.stream().filter(
            m -> m.getIdWorkpack().equals(b.getIdWorkpack()) && m.getIdPlan().equals(b.getIdPlan())).findFirst().ifPresent(x -> {
            x.setBaselineStart(b.getStart());
            x.setBaselineEnd(b.getEnd());
        }));
        return milestoneDetail;
    }


    private DashboardDto getDashboardDto(Long idWorkpack, boolean isDeliverable, final Long idPlan) {
        final WorkpackRepository workpackRepository = applicationContext.getBean(WorkpackRepository.class);
        Set<Long> ids = isDeliverable
                        ? Collections.singleton(idWorkpack)
                        : workpackRepository.findAllChildren(idWorkpack);

        if (ids.isEmpty()) {
            return null;
        }
        final DashboardDto dto = new DashboardDto();
        ids.forEach(id -> {
            DashboardWorkpackDetailDto detail = new DashboardWorkpackDetailDto(mapWorkpackDetail.get(new WorkpackKey(id, idPlan)));
            if (detail.getIdWorkpack() != null) {
                dto.getWorkpacks().add(detail);
            }
        });
        return dto;
    }

    public DashboardMonthDto getDashboardMonthDto(Long idWorkpack, boolean isDeliverable, Long idPlan) {
        DashboardDto dashboard = getDashboardDto(idWorkpack, isDeliverable, idPlan);
        if (dashboard == null || dashboard.getWorkpacks().isEmpty() ) return null;
        return DashboardMonthDto.of(dashboard);
    }
   


}
