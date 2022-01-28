package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateResponse;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetBaselineUpdatesService implements IGetBaselineUpdatesService {

  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;

  private final IGetFirstTimeBaselineUpdatesService getFirstTimeBaselineUpdatesService;

  private final IGetAnotherTimeBaselineUpdatesService getAnotherTimeBaselineUpdatesService;

  @Autowired
  public GetBaselineUpdatesService(
      final BaselineRepository baselineRepository,
      final WorkpackRepository workpackRepository,
      final IGetFirstTimeBaselineUpdatesService getFirstTimeBaselineUpdatesService,
      final IGetAnotherTimeBaselineUpdatesService getAnotherTimeBaselineUpdatesService
  ) {
    this.baselineRepository = baselineRepository;
    this.workpackRepository = workpackRepository;
    this.getFirstTimeBaselineUpdatesService = getFirstTimeBaselineUpdatesService;
    this.getAnotherTimeBaselineUpdatesService = getAnotherTimeBaselineUpdatesService;
  }

  @Override
  public List<UpdateResponse> getUpdates(final Long idWorkpack) {
    final Workpack workpack = this.findProjectWorkpackById(idWorkpack);
    final Baseline baseline = this.baselineRepository.findActiveBaselineByWorkpackId(idWorkpack).orElse(null);

    return baseline == null
        ? this.getFirstTimeBaselineUpdatesService.getUpdates(workpack.getChildren(), false)
        : this.getAnotherTimeBaselineUpdatesService.getUpdates(baseline, workpack);
  }

  private Workpack findProjectWorkpackById(final Long idWorkpack) {
    return this.workpackRepository.findWithPropertiesAndModelAndChildrenById(idWorkpack)
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND))
        .ifIsNotProjectThrowsException();
  }

}
