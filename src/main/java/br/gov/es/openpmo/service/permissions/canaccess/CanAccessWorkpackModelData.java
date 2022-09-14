package br.gov.es.openpmo.service.permissions.canaccess;

import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.service.permissions.canaccess.ICanAccessData.ICanAccessDataResponse;

@Component
public class CanAccessWorkpackModelData implements ICanAccessWorkpackModelData {

  private final WorkpackRepository workpackRepository;
  private final ICanAccessData canAccessData;

  public CanAccessWorkpackModelData(
    final WorkpackRepository workpackRepository,
    final ICanAccessData canAccessData
  ) {
    this.workpackRepository = workpackRepository;
    this.canAccessData = canAccessData;
  }


  @Override
  public ICanAccessDataResponse execute(
    final Long idWorkpackModel,
    final String authorization
  ) {
    final List<Long> workpackIds = this.workpackRepository.findWorkpacksByWorkpackModel(idWorkpackModel).stream()
      .map(Workpack::getId)
      .collect(Collectors.toList());

    return this.canAccessData.execute(workpackIds, authorization);
  }

}
