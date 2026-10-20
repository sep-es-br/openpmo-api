/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.controller.search;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.PageResponse;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.universalSearch.UniversalSearchItemQueryResult;
import br.gov.es.openpmo.dto.universalSearch.UniversalSearchParameters;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization;
import br.gov.es.openpmo.service.workpack.GetTransversalSearchWorkpackIds;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author gean.carneiro
 */
@Api
@RestController
@RequestMapping("search")
public class SearchController {
    
    private final ResponseHandler responseHandler;
    
    private final WorkpackService wpSrv;
    private final IGetPersonFromAuthorization getPersonFromAuthorization;
    private final GetTransversalSearchWorkpackIds getTransversalSearchWorkpackIds;
    
    
    public SearchController(
            final WorkpackService wpSrv,
            final IGetPersonFromAuthorization getPersonFromAuthorization,
            final GetTransversalSearchWorkpackIds getTransversalSearchWorkpackIds,
            final ResponseHandler responseHandler
    ) {
        this.wpSrv = wpSrv;
        this.getPersonFromAuthorization = getPersonFromAuthorization;
        this.getTransversalSearchWorkpackIds = getTransversalSearchWorkpackIds;
        this.responseHandler = responseHandler;
    }
    
    @GetMapping
    public Response<PageResponse<UniversalSearchItemQueryResult>> getDashboard(
        @RequestParam(name = "id-plan") Long planId,
        @RequestParam(name = "id-workpack", required = false) Long workpackId,
        @RequestParam(name = "term", required = false, defaultValue = "") String term,
        @RequestParam int page, @RequestParam int pageSize,
        @Authorization final String authorization){
        
        if(term.trim().length() < 3){
            return this.responseHandler.success(PageResponse.of(Page.empty()));
        }
        
        final IGetPersonFromAuthorization.PersonDataResponse currentPerson =
          this.getPersonFromAuthorization.execute(authorization);
        final Long userId = currentPerson.getPerson().getId();

        UniversalSearchParameters params = new UniversalSearchParameters(
          planId,
          workpackId,
          userId,
          currentPerson.getKey(),
          term
        );
        final java.util.Map<Long, java.util.List<Long>> transversalProgramsByWorkpack =
          this.getTransversalSearchWorkpackIds.findAccessibleWorkpackProgramIds(
          planId,
          currentPerson.getKey(),
          authorization
        );
        params.setTransversalWorkpackIds(new java.util.ArrayList<>(transversalProgramsByWorkpack.keySet()));
        params.setTransversalProgramsByWorkpack(transversalProgramsByWorkpack);
        params.setTransversalProgramIds(transversalProgramsByWorkpack.values().stream()
          .flatMap(java.util.Collection::stream).distinct().collect(java.util.stream.Collectors.toList()));

        return this.responseHandler.success(PageResponse.of(this.wpSrv.doUniversalSearch(params, PageRequest.of(page, pageSize))));
    }
}
