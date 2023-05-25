package br.gov.es.openpmo.controller.actor;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.person.favorite.WorkpackFavoritedDetail;
import br.gov.es.openpmo.dto.person.favorite.WorkpackFavoritedRequest;
import br.gov.es.openpmo.service.actors.IsFavoritedByService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;

@Api
@CrossOrigin
@RestController
@RequestMapping("/workpack")
public class IsFavoritedByController {

  private final IsFavoritedByService isFavoritedByService;

  private final ICanAccessService canAccessService;

  public IsFavoritedByController(
    final IsFavoritedByService isFavoritedByService,
    final ICanAccessService canAccessService
  ) {
    this.isFavoritedByService = isFavoritedByService;
    this.canAccessService = canAccessService;
  }

  @Transactional
  @PatchMapping("/{id-workpack}/favorite")
  public ResponseEntity<ResponseBase<Void>> favorite(
    @PathVariable("id-workpack") final Long idWorkpack,
    @RequestBody @Valid final WorkpackFavoritedRequest request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(idWorkpack, authorization);
    this.isFavoritedByService.toggleFavorite(idWorkpack, request, authorization);
    return ResponseEntity.ok(ResponseBase.success());
  }

  @GetMapping("/favorites")
  public ResponseEntity<ResponseBase<Set<WorkpackFavoritedDetail>>> findAllFavorites(
    @RequestParam(value = "id-plan") final Long idPlan,
    @Authorization final String authorization
  ) {
    final Set<WorkpackFavoritedDetail> response = this.isFavoritedByService.findAllFavoritesWorkpack(authorization, idPlan);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

}
