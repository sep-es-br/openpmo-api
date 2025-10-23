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
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.Assert;
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
    public Response<PageResponse<UniversalSearchItemQueryResult>> getDashboard(
        @RequestParam(name = "id-plan") Long planId,
        @RequestParam(name = "id-workpack", required = false) Long workpackId,
        @RequestParam(name = "term", required = false, defaultValue = "") String term,
        @RequestParam int page, @RequestParam int pageSize,
        @Authorization final String authorization){
        
        Assert.isTrue(term.trim().length() >= 3, "O texto da busca deve ter ao menos 3 caracteres");
        
        final Long userId = this.tokenService.getUserId(authorization);

        UniversalSearchParameters params = new UniversalSearchParameters(planId, workpackId, userId, term);

        return this.responseHandler.success(PageResponse.of(this.wpSrv.doUniversalSearch(params, PageRequest.of(page, pageSize))));
    }
}
