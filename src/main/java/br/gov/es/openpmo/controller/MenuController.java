package br.gov.es.openpmo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.menu.MenuOfficeDto;
import br.gov.es.openpmo.dto.menu.WorkpackMenuDto;
import br.gov.es.openpmo.service.MenuService;

@RestController
@CrossOrigin
@RequestMapping(value = "/menus")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/office")
    public ResponseEntity<ResponseBase<List<MenuOfficeDto>>> indexOffice() {
        List<MenuOfficeDto> offices = menuService.findAllOffice();
        if (offices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        ResponseBase<List<MenuOfficeDto>> response = new ResponseBase<List<MenuOfficeDto>>().setData(offices).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/portfolio")
    public ResponseEntity<ResponseBase<List<WorkpackMenuDto>>> indexPortfolio(@RequestParam(value = "id-office") Long idOffice) {
        List<WorkpackMenuDto> portfolios = menuService.findAllPortfolio(idOffice);
        if (portfolios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        ResponseBase<List<WorkpackMenuDto>> response = new ResponseBase<List<WorkpackMenuDto>>().setData(portfolios).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

}
