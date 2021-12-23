package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.model.workpacks.Workpack;

import java.util.Optional;

public interface BreadcrumbWorkpackHelper {

  Workpack findByIdWithParent(Long id);

  WorkpackDetailDto getWorkpackDetailDto(Workpack workpack);

  Optional<WorkpackName> findWorkpackNameAndFullname(Long idWorkpack);
}
