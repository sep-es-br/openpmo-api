package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateResponse;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.enumerator.BaselineStatus;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GetFirstTimeBaselineUpdatesService implements IGetFirstTimeBaselineUpdatesService {

  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;

  @Autowired
  public GetFirstTimeBaselineUpdatesService(
    final BaselineRepository baselineRepository,
    final WorkpackRepository workpackRepository
  ) {
    this.baselineRepository = baselineRepository;
    this.workpackRepository = workpackRepository;
  }

  private static String getIcon(final Workpack workpack) {
    final WorkpackModel workpackModel = workpack.getWorkpackModelInstance();
    return Optional.ofNullable(workpackModel).map(WorkpackModel::getFontIcon).orElse("");
  }

  @Nullable
  private UpdateResponse getUpdate(
    final Workpack workpack,
    final boolean isSnapshot
  ) {
    UpdateResponse result = null;
    final Workpack master = this.getWorkpack(workpack, isSnapshot);

    if(master != null) {
      result = new UpdateResponse(
        master.getId(),
        getIcon(master),
        this.getDescription(master),
        BaselineStatus.NEW,
        null
      );
    }

    return result;
  }

  private String getDescription(final Workpack workpack) {
    return Optional.ofNullable(workpack).map(this::getWorkpackName).orElse("");
  }

  @Override
  public List<UpdateResponse> getUpdates(
    final Iterable<? extends Workpack> workpacks,
    final boolean isSnapshot
  ) {
    if(workpacks == null) {
      return Collections.emptyList();
    }

    final List<UpdateResponse> updates = new ArrayList<>();

    for(final Workpack workpack : workpacks) {
      if(workpack.isDeliverable() || workpack.isMilestone()) {
        Optional.ofNullable(this.getUpdate(workpack, isSnapshot)).ifPresent(updates::add);
      }
      updates.addAll(this.getUpdates(workpack.getChildren(), isSnapshot));
    }

    return updates;
  }

  private String getWorkpackName(final Workpack workpack) {
    return this.workpackRepository.findWorkpackNameAndFullname(workpack.getId())
      .map(WorkpackName::getName)
      .orElse(null);
  }

  @Nullable
  private Workpack getWorkpack(
    final Workpack workpack,
    final boolean isSnapshot
  ) {
    final Workpack master = this.baselineRepository.findMasterBySnapshotId(workpack.getId()).orElse(null);
    return isSnapshot
      ? master
      : workpack;
  }

}
