package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.modelmapper.ModelMapper;

public class ProgramParamDto extends WorkpackParamDto {

  @Override
  public Workpack getWorkpack(ModelMapper modelMapper) {
    return modelMapper.map(this, Program.class);
  }

}
