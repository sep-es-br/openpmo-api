package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.modelmapper.ModelMapper;

public class DeliverableParamDto extends WorkpackParamDto {

  @Override
  public Workpack getWorkpack(ModelMapper modelMapper) {
    return modelMapper.map(this, Deliverable.class);
  }

}
