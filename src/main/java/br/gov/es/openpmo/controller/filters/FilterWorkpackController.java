package br.gov.es.openpmo.controller.filters;

import br.gov.es.openpmo.model.filter.CustomFilterEnum;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.filters.CustomFilterService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@CrossOrigin
@RequestMapping("/filter/workpackModels/{idWorkpackModel}/workpacks")
public class FilterWorkpackController extends CreateAndUpdateUsingWorkpackModelFilterOperations {

  private final CustomFilterService customFilterService;
  private final TokenService tokenService;

  @Autowired
  public FilterWorkpackController(
    final CustomFilterService customFilterService,
    final TokenService tokenService
  ) {
    this.customFilterService = customFilterService;
    this.tokenService = tokenService;
  }

  @Override
  protected CustomFilterService getCustomFilterService() {
    return this.customFilterService;
  }

  @Override
  protected TokenService getTokenService() {
    return this.tokenService;
  }

  @Override
  protected CustomFilterEnum getFilter() {
    return CustomFilterEnum.WORKPACK;
  }

}
