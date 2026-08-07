package br.gov.es.openpmo.controller.plugins;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.service.agreements.AgreementProviderService;
import br.gov.es.openpmo.service.obligations.ObligationProviderService;
import br.gov.es.openpmo.service.procurements.ProcurementProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/plugins")
public class PluginAvailabilityController {

  private final AgreementProviderService agreementProviderService;
  private final ProcurementProviderService procurementProviderService;
  private final ObligationProviderService obligationProviderService;

  public PluginAvailabilityController(
    final AgreementProviderService agreementProviderService,
    final ProcurementProviderService procurementProviderService,
    final ObligationProviderService obligationProviderService
  ) {
    this.agreementProviderService = agreementProviderService;
    this.procurementProviderService = procurementProviderService;
    this.obligationProviderService = obligationProviderService;
  }

  @GetMapping("/availability")
  public ResponseEntity<ResponseBase<Map<String, Boolean>>> getAvailability() {
    final Map<String, Boolean> availability = new LinkedHashMap<>();
    availability.put("agreements", this.agreementProviderService.isAvailable());
    availability.put("procurements", this.procurementProviderService.isAvailable());
    availability.put("obligations", this.obligationProviderService.isAvailable());
    return ResponseEntity.ok(ResponseBase.of(availability));
  }
}
