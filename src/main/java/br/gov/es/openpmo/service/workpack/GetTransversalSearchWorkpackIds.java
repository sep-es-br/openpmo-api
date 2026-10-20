package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.TransversalContextDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalStructuralLinkDto;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessData;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class GetTransversalSearchWorkpackIds {

  private final TransversalProjectService transversalProjectService;
  private final ICanAccessData canAccessData;

  public GetTransversalSearchWorkpackIds(
    final TransversalProjectService transversalProjectService,
    final ICanAccessData canAccessData
  ) {
    this.transversalProjectService = transversalProjectService;
    this.canAccessData = canAccessData;
  }

  public List<Long> execute(final Long planId, final String personKey, final String authorization) {
    return List.copyOf(this.findAccessibleWorkpackProgramIds(planId, personKey, authorization).keySet());
  }

  public Map<Long, List<Long>> findAccessibleWorkpackProgramIds(
    final Long planId,
    final String personKey,
    final String authorization
  ) {
    if (planId == null || personKey == null) {
      return Collections.emptyMap();
    }

    final Map<Long, List<Long>> programsByWorkpack = new HashMap<>();
    for (final TransversalContextDto context : this.transversalProjectService.findStakeholderAccessContexts(personKey)) {
      if (!planId.equals(context.getIdPlan())) {
        continue;
      }
      for (final TransversalStructuralLinkDto link : this.transversalProjectService
        .findStructuralLinksForProgramTree(context.getIdTransversalProgram(), true)) {
        if (this.canAccessData.execute(link.getIdWorkpack(), authorization).canReadResource()) {
          programsByWorkpack.computeIfAbsent(link.getIdWorkpack(), ignored -> new ArrayList<>())
            .add(link.getIdTransversalProgram());
        }
      }
    }
    programsByWorkpack.replaceAll((id, programs) -> programs.stream().distinct().collect(java.util.stream.Collectors.toList()));
    return programsByWorkpack;
  }

}
