package br.gov.es.openpmo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.menu.BreadcrumbDto;
import br.gov.es.openpmo.service.BreadcrumbService;

@RestController
@CrossOrigin
@RequestMapping(value = "/breadcrumbs")
public class BreadcrumbController {

    private final BreadcrumbService breadcrumbService;

    @Autowired
    public BreadcrumbController(BreadcrumbService breadcrumbService) {
        this.breadcrumbService = breadcrumbService;
    }

    @GetMapping("/locality/{id}")
    public ResponseEntity<ResponseBase<List<BreadcrumbDto>>> locality(@PathVariable Long id) {
        List<BreadcrumbDto> breadcrumbLocalities = breadcrumbService.localities(id);
        ResponseBase<List<BreadcrumbDto>> response = new ResponseBase<List<BreadcrumbDto>>().setData(
            breadcrumbLocalities).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/workpack/{id}")
    public ResponseEntity<ResponseBase<List<BreadcrumbDto>>> workpack(@PathVariable Long id) {
        List<BreadcrumbDto> breadcrumbLocalities = breadcrumbService.workpacks(id);
        ResponseBase<List<BreadcrumbDto>> response = new ResponseBase<List<BreadcrumbDto>>().setData(
            breadcrumbLocalities).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/model/{id}")
    public ResponseEntity<ResponseBase<List<BreadcrumbDto>>> model(@PathVariable Long id) {
        List<BreadcrumbDto> breadcrumbLocalities = breadcrumbService.models(id);
        ResponseBase<List<BreadcrumbDto>> response = new ResponseBase<List<BreadcrumbDto>>().setData(
            breadcrumbLocalities).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

}
