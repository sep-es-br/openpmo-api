package br.gov.es.openpmo.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;

import br.gov.es.openpmo.dto.menu.WorkpackMenuResultDto;
import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.service.workpack.PortifolioService;

public class ApplicationCacheUtil {

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<Long, List<WorkpackResultDto>> mapPlanWorkpackResult = new HashMap<>(0);
    private final Map<Long, String> mapHashCode = new HashMap<>(0);
    private final Map<Long, Boolean> mapPlanLoading = new HashMap<>(0);
    private boolean loadingAll;

    @PostConstruct
    public void loadAllPlan() {
        loadAllCache();
    }

    public boolean isPlanChanged(Long idPlan) {
        PortifolioService portifolioService = applicationContext.getBean(PortifolioService.class);
        if (mapHashCode.get(idPlan) == null) {
            return true;
        }
        String hashCode = portifolioService.getHashCodeMenuCustomByIdPlan(idPlan);
        return !mapHashCode.get(idPlan).equals(hashCode);
    }

    @Async
    public void loadAllCache() {
        loadingAll = true;
        PortifolioService portifolioService = applicationContext.getBean(PortifolioService.class);
        List<Long> planIds = portifolioService.findAllPlanIds();
        for (Long planId : planIds) {
            String hashCode = portifolioService.getHashCodeMenuCustomByIdPlan(planId);
            String hash = mapHashCode.get(planId);
            if (!hashCode.equals(hash)) {
                mapHashCode.put(planId, hashCode);
                List<WorkpackResultDto> list = portifolioService.findAllMenuCustomByIdPlan(planId);
                mapPlanWorkpackResult.put(planId, list);
            }
        }
        loadingAll = false;
    }

    public void loadResultByPlan(Long idPlan) {
        mapPlanLoading.put(idPlan, true);
        PortifolioService portifolioService = applicationContext.getBean(PortifolioService.class);
        String hashCode = portifolioService.getHashCodeMenuCustomByIdPlan(idPlan);
        mapHashCode.put(idPlan, hashCode);
        List<WorkpackResultDto> list = portifolioService.findAllMenuCustomByIdPlan(idPlan);
        mapPlanWorkpackResult.put(idPlan, list);
        mapPlanLoading.put(idPlan, false);
    }

    public List<WorkpackMenuResultDto> getListWorkpackMenuResultDto(Long idPlan) {
        if (isPlanChanged(idPlan) && !loadingAll && !Boolean.TRUE.equals(this.mapPlanLoading.get(idPlan))) {
            loadResultByPlan(idPlan);
        }
        List<WorkpackMenuResultDto> list = new ArrayList<>(0);
        mapPlanWorkpackResult.get(idPlan).forEach(w -> list.add(new WorkpackMenuResultDto(w)));
        return list;
    }

    public List<WorkpackResultDto> getListWorkpackResultDtoByPlan(Long idPlan) {
        if (isPlanChanged(idPlan) && !loadingAll && !Boolean.TRUE.equals(this.mapPlanLoading.get(idPlan))) {
            loadResultByPlan(idPlan);
        }
        List<WorkpackResultDto> list = new ArrayList<>(0);
        mapPlanWorkpackResult.get(idPlan).forEach(w -> list.add(new WorkpackResultDto(w)));
        return list;
    }

    public List<Long> getListIdWorkpackWithParent(Long idWorkpack) {
        loadPlanIfChanged(idWorkpack);
        List<Long> ids = new ArrayList<>(0);
        ids.add(idWorkpack);
        Set<WorkpackResultDto> list = mapPlanWorkpackResult.values().stream().flatMap(
            Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new));
        list.stream().filter(w -> w.getId().equals(idWorkpack)).findFirst().ifPresent(
            actual -> addParentId(ids, actual, list));
        return ids;
    }

    public List<Long> getListIdWorkpackChildren(Long idWorkpack, Long idPlan) {
        List<Long> ids = new ArrayList<>(0);
        WorkpackResultDto actual = getWorkpackBreakdownStructure(idWorkpack, idPlan, true);
        addChildrenId(ids, actual);
        return ids;
    }

    public WorkpackResultDto getWorkpackBreakdownStructure(Long idWorkpack, Long idPlan, boolean allLevels) {
        loadPlanIfChanged(idWorkpack);
        Set<WorkpackResultDto> list =
            idPlan != null
            ? this.mapPlanWorkpackResult.get(idPlan).stream().map(WorkpackResultDto::new).collect(
                Collectors.toCollection(LinkedHashSet::new))
            : mapPlanWorkpackResult.values().stream().flatMap(Collection::stream).map(WorkpackResultDto::new).collect(
                Collectors.toCollection(LinkedHashSet::new));

        if (!allLevels) {
            WorkpackResultDto workpack = list.stream().filter(w -> w.getId().equals(idWorkpack)).findFirst().orElse(null);
            if (workpack == null) return null;
            workpack.getChildren().addAll(list.stream().filter(w -> workpack.getId().equals(w.getIdParent())).collect(
                Collectors.toList()));
            return workpack;
        }
        return this.getWorkpackResultDto(idWorkpack, list);
    }

    private WorkpackResultDto getWorkpackResultDto(Long idWorkpack, Set<WorkpackResultDto> list) {
        WorkpackResultDto workpack = list.stream().filter(w -> w.getId().equals(idWorkpack)).findFirst().orElse(null);
        if (workpack == null) return null;
        workpack.setChildren(getChildren(workpack, list));
        return workpack;
    }

    private Set<WorkpackResultDto> getChildren(WorkpackResultDto workpack, Set<WorkpackResultDto> list) {
        Set<WorkpackResultDto> children = list.stream().filter(w -> workpack.getId().equals(w.getIdParent())).collect(
            Collectors.toCollection(LinkedHashSet::new));
        if (CollectionUtils.isNotEmpty(children)) {
            for (WorkpackResultDto child : children) {
                child.setChildren(this.getChildren(child, list));
            }
        }
        return children;
    }


    public boolean hasChilcren(Long idWorkpack) {
        Set<WorkpackResultDto> list = mapPlanWorkpackResult.values().stream().flatMap(Collection::stream).map(
            WorkpackResultDto::new).collect(Collectors.toCollection(LinkedHashSet::new));
        WorkpackResultDto workpack = list.stream().filter(w -> w.getId().equals(idWorkpack)).findFirst().orElse(null);
        return workpack != null && list.stream().anyMatch(w -> workpack.getId().equals(w.getIdParent()));
    }

    private void loadPlanIfChanged(Long idWorkpack) {
        Long idPlan = null;
        for (Map.Entry<Long, List<WorkpackResultDto>> entry : mapPlanWorkpackResult.entrySet()) {
            if (entry.getValue().stream().anyMatch(w -> w.getIdPlan().equals(idWorkpack))) {
                idPlan = entry.getKey();
                break;
            }
        }
        if (idPlan != null) {
            if (isPlanChanged(idPlan) && !loadingAll && !Boolean.TRUE.equals(this.mapPlanLoading.get(idPlan))) {
                loadResultByPlan(idPlan);
            }
        }
    }

    private void addParentId(List<Long> ids, WorkpackResultDto actual, Set<WorkpackResultDto> list) {
        if (actual.getIdParent() != null) {
            WorkpackResultDto parent = list.stream().filter(w -> w.getId().equals(actual.getIdParent()))
                                               .findFirst().orElse(null);
            if (parent != null) {
                ids.add(parent.getId());
                if (parent.getIdParent() != null) {
                    addParentId(ids, parent, list);
                }
            }
        }
    }

    private void addChildrenId(List<Long> ids, WorkpackResultDto actual) {
        if (actual != null && CollectionUtils.isNotEmpty(actual.getChildren())) {
            for (WorkpackResultDto child : actual.getChildren()) {
                ids.add(child.getId());
                if (CollectionUtils.isNotEmpty(child.getChildren())) {
                    this.addChildrenId(ids, child);
                }
            }
        }
    }

}
