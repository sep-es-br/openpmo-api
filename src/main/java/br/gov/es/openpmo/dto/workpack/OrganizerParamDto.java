package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Organizer;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.modelmapper.ModelMapper;

public class OrganizerParamDto extends WorkpackParamDto {

  @Override
  public Workpack getWorkpack(ModelMapper modelMapper) {
    return modelMapper.map(this, Organizer.class);
  }

}
