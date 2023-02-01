package br.gov.es.openpmo.controller.ccbmembers;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.ccbmembers.CCBMemberRequest;
import br.gov.es.openpmo.dto.ccbmembers.CCBMemberResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ICCBMemberController {

    @GetMapping("/{id-workpack}/workpack")
    Response<List<CCBMemberResponse>> getAll(@PathVariable("id-workpack") Long workpackId,
            @RequestHeader(name = "Authorization") final String authorization);

    @GetMapping
    Response<CCBMemberResponse> getCCBMember(
            @RequestParam("id-person") Long idPerson,
            @RequestParam("id-workpack") Long idWorkpack,
            @RequestParam("id-plan") Long idPlan,
            @Authorization final String authorization);

    @Transactional
    @PostMapping
    Response<Void> createRelationship(@RequestBody CCBMemberRequest request,
            @RequestHeader(name = "Authorization") final String authorization);

    @Transactional
    @PutMapping
    Response<Void> updateRelationship(@RequestBody CCBMemberRequest request,
            @RequestHeader(name = "Authorization") final String authorization);

    @Transactional
    @DeleteMapping
    Response<Void> delete(
            @RequestParam("id-person") Long idPerson,
            @RequestParam("id-workpack") Long idWorkpack,
            @RequestHeader(name = "Authorization") final String authorization);

}
