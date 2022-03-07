package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostDataChart;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.ScheduleDataChart;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.ScopeDataChart;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.util.Optional;

import static br.gov.es.openpmo.model.dashboards.DashboardUtils.apply;

public class TripleConstraintData {

    private Long idBaseline;

    private LocalDate mesAno;

    private CostData cost;

    private ScheduleData schedule;

    private ScopeData scope;

    public static TripleConstraintData of(TripleConstraintDataChart from) {
        if (from == null) {
            return null;
        }

        final TripleConstraintData to = new TripleConstraintData();

        to.setIdBaseline(from.getIdBaseline());
        to.setMesAno(from.getMesAno());

        apply(from.getCost(), CostData::of, to::setCost);
        apply(from.getSchedule(), ScheduleData::of, to::setSchedule);
        apply(from.getScope(), ScopeData::of, to::setScope);
        return to;
    }

    public CostData getCost() {
        return cost;
    }

    public void setCost(CostData cost) {
        this.cost = cost;
    }

    public ScheduleData getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleData schedule) {
        this.schedule = schedule;
    }

    public ScopeData getScope() {
        return scope;
    }

    public void setScope(ScopeData scope) {
        this.scope = scope;
    }

    @Transient
    public TripleConstraintDataChart getResponse() {
        final TripleConstraintDataChart response = new TripleConstraintDataChart();

        response.setIdBaseline(this.idBaseline);
        response.setMesAno(this.mesAno);

        response.setCost(getCostDataChart());
        response.setSchedule(getScheduleDataChart());
        response.setScope(getScopeDataChart());

        return response;
    }

    @Transient
    private CostDataChart getCostDataChart() {
        return Optional.ofNullable(this.cost)
                .map(CostData::getResponse)
                .orElse(null);
    }

    @Transient
    private ScheduleDataChart getScheduleDataChart() {
        return Optional.ofNullable(this.schedule)
                .map(ScheduleData::getResponse)
                .orElse(null);
    }

    @Transient
    private ScopeDataChart getScopeDataChart() {
        return Optional.ofNullable(this.scope)
                .map(ScopeData::getResponse)
                .orElse(null);
    }

    public Long getIdBaseline() {
        return idBaseline;
    }

    public void setIdBaseline(Long idBaseline) {
        this.idBaseline = idBaseline;
    }

    public LocalDate getMesAno() {
        return mesAno;
    }

    public void setMesAno(LocalDate mesAno) {
        this.mesAno = mesAno;
    }
}
