package br.gov.es.openpmo.controller.plugins;

import br.gov.es.openpmo.sigef_core.model.ISigefProvider;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/sigef-selections")
public class SigefSelectionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(SigefSelectionController.class);

  private final Optional<ISigefProvider> sigefProvider;

  public SigefSelectionController(Optional<ISigefProvider> sigefProvider) {
    this.sigefProvider = sigefProvider;
  }

  @GetMapping("/budget-units")
  public ResponseEntity<Object> getBudgetUnits() {
    return this.execute(ISigefProvider::getBudgetUnitList);
  }

  @GetMapping("/budget-plans")
  public ResponseEntity<Object> getBudgetPlans(@RequestParam("budgetUnitCode") String budgetUnitCode) {
    return this.execute(provider -> provider.getBudgetPlanList(budgetUnitCode));
  }

  @GetMapping("/financial-sources")
  public ResponseEntity<Object> getFinancialSources() {
    return this.execute(ISigefProvider::getResourceSourceList);
  }

  private ResponseEntity<Object> execute(SigefRequest request) {
    if (!sigefProvider.isPresent()) {
      return ResponseEntity.noContent().build();
    }
    try {
      JsonNode result = request.execute(sigefProvider.get());
      return result == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    } catch (Exception exception) {
      UUID errorId = UUID.randomUUID();
      LOGGER.error(errorId.toString(), exception);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorId + " - " + exception.getMessage());
    }
  }

  @FunctionalInterface
  private interface SigefRequest {
    JsonNode execute(ISigefProvider provider) throws Exception;
  }
}
