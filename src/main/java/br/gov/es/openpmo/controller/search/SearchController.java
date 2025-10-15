/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.controller.search;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.dashboards.v2.DashboardResponse;
import br.gov.es.openpmo.dto.universalSearch.UniversalSearchItemQueryResult;
import br.gov.es.openpmo.dto.universalSearch.UniversalSearchParameters;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import java.time.YearMonth;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

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
    private final TokenService tokenService;
    
    
    public SearchController(
            final WorkpackService wpSrv,
            final TokenService tokenSrv,
            final ResponseHandler responseHandler
    ) {
        this.wpSrv = wpSrv;
        this.tokenService = tokenSrv;
        this.responseHandler = responseHandler;
    }
    
    @GetMapping
    public Response<List<UniversalSearchItemQueryResult>> getDashboard(
        @RequestParam(name = "id-plan") Long planId,
        @RequestParam(name = "id-workpack", required = false) Long workpackId,
        @RequestParam(name = "term", required = false, defaultValue = "") String term,
        @Authorization final String authorization){
        
        final Long userId = this.tokenService.getUserId(authorization);

        UniversalSearchParameters params = new UniversalSearchParameters(planId, workpackId, userId, term);

        return this.responseHandler.success(this.wpSrv.doUniversalSearch(params));
    }
}
