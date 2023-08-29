package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.repository.UnitMeasureRepository;
import org.springframework.stereotype.Component;

import static br.gov.es.openpmo.utils.ApplicationMessage.UNITMEASURE_NOT_FOUND;

@Component
public class GetUnitMeasureScaleByWorkpack {

  private final UnitMeasureRepository unitMeasureRepository;

  public GetUnitMeasureScaleByWorkpack(final UnitMeasureRepository unitMeasureRepository) {this.unitMeasureRepository =
    unitMeasureRepository;}

  public int execute(final Long idWorkpack) {
    final UnitMeasure unitMeasure = this.unitMeasureRepository.findByWorkpackId(idWorkpack)
      .orElseThrow(() -> new NegocioException(UNITMEASURE_NOT_FOUND));
    return Math.toIntExact(unitMeasure.getPrecision());
  }

}
