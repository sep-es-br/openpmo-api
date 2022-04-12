package br.gov.es.openpmo.controller.ccbmembers;

import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.ccbmembers.CCBMemberRequest;
import br.gov.es.openpmo.dto.ccbmembers.CCBMemberResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ICCBMemberController {

  @GetMapping("/{id-workpack}/workpack")
  Response<List<CCBMemberResponse>> getAll(@PathVariable("id-workpack") Long workpackId);

  @GetMapping
  Response<CCBMemberResponse> getCCBMember(
      @RequestParam("id-person") Long idPerson,
      @RequestParam("id-workpack") Long idWorkpack,
      @RequestParam("id-plan") Long idPlan
  );

  @Transactional
  @PostMapping
  Response<Void> createRelationship(@RequestBody CCBMemberRequest request);

  @Transactional
  @PutMapping
  Response<Void> updateRelationship(@RequestBody CCBMemberRequest request);

  @Transactional
  @DeleteMapping
  Response<Void> delete(
      @RequestParam("id-person") Long idPerson,
      @RequestParam("id-workpack") Long idWorkpack
  );

}