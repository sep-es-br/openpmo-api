package br.gov.es.openpmo.controller.baselines;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.baselines.*;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.BaselineDetailCCBMemberResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

public interface IBaselineController {

    @GetMapping
    Response<List<GetAllBaselinesResponse>> getAllByWorkpackId(@RequestParam("id-workpack") Long idWorkpack);

    @GetMapping("/ccb-member")
    Response<List<GetAllCCBMemberBaselineResponse>> getAllByPersonId(@RequestHeader(name = "Authorization") String authorization);

    @GetMapping("/updates")
    Response<List<UpdateResponse>> getUpdates(@RequestParam("id-workpack") Long idWorkpack);

    @Transactional
    @PostMapping
    Response<EntityDto> create(
            @RequestBody IncludeBaselineRequest request,
            @RequestHeader(name = "Authorization") String authorization
    );

    @Transactional
    @PutMapping("/{id-baseline}/submit")
    Response<Void> submit(
            @PathVariable("id-baseline") Long idBaseline,
            @RequestBody SubmitBaselineRequest request,
            String authorization
    );

    @Transactional
    @PutMapping("/{id-baseline}")
    Response<Void> edit(
            @PathVariable("id-baseline") Long idBaseline,
            @RequestBody EditDraftBaselineRequest request
    );

    @Transactional
    @DeleteMapping("/{id-baseline}")
    Response<Void> delete(@PathVariable("id-baseline") Long idBaseline);

    @Transactional
    @PostMapping("/submit-cancelling")
    Response<EntityDto> submitCancelling(
            @RequestBody @Valid SubmitCancellingRequest request,
            @RequestHeader(name = "Authorization") String authorization
    );

    @GetMapping("/{id-baseline}")
    Response<BaselineDetailResponse> getById(
            @PathVariable("id-baseline") Long idBaseline
    );

    @GetMapping("/{id-baseline}/ccb-member-view")
    Response<BaselineDetailCCBMemberResponse> getBaselineByIdAsCCBMemberView(
            @RequestHeader(name = "Authorization") String authorization,
            @PathVariable("id-baseline") Long idBaseline
    );

    @Transactional
    @PutMapping("/{id-baseline}/evaluate")
    Response<Void> evaluateBaseline(
            @RequestHeader(name = "Authorization") String authorization,
            @PathVariable("id-baseline") Long idBaseline,
            @RequestBody BaselineEvaluationRequest request
    );

}
