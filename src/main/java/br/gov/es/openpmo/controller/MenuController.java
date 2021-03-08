package br.gov.es.openpmo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.menu.MenuOfficeDto;
import br.gov.es.openpmo.dto.menu.WorkpackMenuDto;
import br.gov.es.openpmo.model.domain.TokenType;
import br.gov.es.openpmo.service.MenuService;
import br.gov.es.openpmo.service.TokenService;

@RestController
@CrossOrigin
@RequestMapping(value = "/menus")
public class MenuController {

    private final MenuService menuService;
    private final TokenService tokenService;

    @Autowired
    public MenuController(MenuService menuService, TokenService tokenService) {
        this.menuService = menuService;
        this.tokenService = tokenService;
    }

    @GetMapping("/office")
    public ResponseEntity<ResponseBase<List<MenuOfficeDto>>> indexOffice(@RequestHeader(name="Authorization") String autorization) {
        String token = autorization.substring(7);
        Long idUser = tokenService.getPersonId(token, TokenType.AUTHENTICATION);
        List<MenuOfficeDto> offices = menuService.findAllOffice(idUser);
        if (offices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        ResponseBase<List<MenuOfficeDto>> response = new ResponseBase<List<MenuOfficeDto>>().setData(offices).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/portfolio")
    public ResponseEntity<ResponseBase<List<WorkpackMenuDto>>> indexPortfolio(@RequestParam(value = "id-office") Long idOffice, @RequestHeader(name="Authorization") String autorization) {
        String token = autorization.substring(7);
        Long idUser = tokenService.getPersonId(token, TokenType.AUTHENTICATION);
        List<WorkpackMenuDto> portfolios = menuService.findAllPortfolio(idOffice, idUser);
        if (portfolios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        ResponseBase<List<WorkpackMenuDto>> response = new ResponseBase<List<WorkpackMenuDto>>().setData(portfolios).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

}
