package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.modelmapper.ModelMapper;

public class MilestoneParamDto extends WorkpackParamDto {

  @Override
  public Workpack getWorkpack(ModelMapper modelMapper) {
    return modelMapper.map(this, Milestone.class);
  }

}
