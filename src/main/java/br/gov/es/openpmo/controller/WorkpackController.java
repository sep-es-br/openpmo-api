package br.gov.es.openpmo.controller;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseWorkpack;
import br.gov.es.openpmo.dto.ResponseBaseWorkpackDetail;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.dto.workpack.WorkpackParamDto;
import br.gov.es.openpmo.model.Workpack;
import br.gov.es.openpmo.model.WorkpackModel;
import br.gov.es.openpmo.model.domain.TokenType;
import br.gov.es.openpmo.service.TokenService;
import br.gov.es.openpmo.service.WorkpackService;

@RestController
@CrossOrigin
@RequestMapping(value = "/workpack")
public class WorkpackController {

	private final WorkpackService workpackService;
	private final TokenService tokenService;
	private static final String SUCESSO = "Success";

	@Autowired
	public WorkpackController(WorkpackService workpackService,
							  TokenService tokenService) {
		this.workpackService = workpackService;
		this.tokenService = tokenService;
	}

	@GetMapping
	public ResponseEntity<ResponseBaseWorkpack> indexBase(@RequestParam(value = "id-plan") Long idPlan,
			@RequestParam(value = "id-plan-model", required = false) Long idPlanModel,
			@RequestParam(value = "id-workpack-model", required = false) Long idWorkPackModel,
		  	@RequestHeader(name="Authorization") String autorization) {
		String token = autorization.substring(7);
		Long idUser = tokenService.getPersonId(token, TokenType.AUTHENTICATION);
		List<WorkpackDetailDto> workpackList = new ArrayList<>();
		List<Workpack> workpacks = workpackService.findAll(idPlan, idPlanModel, idWorkPackModel);
		if (workpacks.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		for (Workpack workpack : workpacks) {
			workpackList.add(workpackService.getWorkpackDetailDto(workpack));
		}
		workpackList = workpackService.chekPermission(workpackList, idUser, idPlan);
		
		if (workpackList.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		ResponseBaseWorkpack base = new ResponseBaseWorkpack().setData(workpackList).setMessage(SUCESSO).setSuccess(true);
		return ResponseEntity.status(200).body(base);
	}

	@GetMapping("/parent")
	public ResponseEntity<ResponseBaseWorkpack> indexBase(@RequestParam(value = "id-plan") Long idPlan,
														  @RequestParam(value = "id-plan-model", required = false) Long idPlanModel,
														  @RequestParam(value = "id-workpack-model", required = false) Long idWorkPackModel,
														  @RequestParam(value = "id-workpack-parent") Long idWorkPackParent,
														  @RequestHeader(name="Authorization") String autorization) {
		String token = autorization.substring(7);
		Long idUser = tokenService.getPersonId(token, TokenType.AUTHENTICATION);
		List<WorkpackDetailDto> workpackList = new ArrayList<>();
		List<Workpack> workpacks = workpackService.findAll(idPlan, idPlanModel, idWorkPackModel, idWorkPackParent);
		if (idWorkPackModel != null) {
			WorkpackModel workpackModel = workpackService.getWorkpackModelById(idWorkPackModel);
			if (workpackModel != null && workpackModel.getSortBy() != null) {
				workpacks.sort((a, b) -> workpackService.compare(workpackService.getValueProperty(a, workpackModel.getSortBy()), workpackService.getValueProperty(b, workpackModel.getSortBy())));
			}
		}
		for (Workpack workpack : workpacks) {
			workpackList.add(workpackService.getWorkpackDetailDto(workpack));
		}
		if (workpackList.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		workpackList = workpackService.chekPermission(workpackList, idUser, idPlan);

		if (workpackList.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		ResponseBaseWorkpack base = new ResponseBaseWorkpack().setData(workpackList).setMessage(SUCESSO).setSuccess(true);
		return ResponseEntity.status(200).body(base);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseBaseWorkpackDetail> find(@PathVariable Long id, @RequestHeader(name="Authorization") String autorization) {
		String token = autorization.substring(7);
		Long idUser = tokenService.getPersonId(token, TokenType.AUTHENTICATION);
		Workpack workpack = workpackService.findById(id);
		WorkpackDetailDto workpackDetailDto = workpackService.getWorkpackDetailDto(workpack);
		workpackDetailDto.setPermissions(workpackService.getPermissionDto(workpackDetailDto, idUser));
		ResponseBaseWorkpackDetail base = new ResponseBaseWorkpackDetail().setData(workpackDetailDto)
				.setMessage(SUCESSO).setSuccess(true);
		return ResponseEntity.status(200).body(base);
	}

	@PostMapping
	public ResponseEntity<ResponseBase<EntityDto>> save(@RequestBody @Valid WorkpackParamDto workpackParamDto) {
		Workpack workpack = workpackService.getWorkpack(workpackParamDto);
		workpack = workpackService.save(workpack);
		ResponseBase<EntityDto> response = new ResponseBase<EntityDto>().setData(new EntityDto(workpack.getId()))
				.setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@PutMapping
	public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid WorkpackParamDto workpackParamDto) {
		Workpack workpack = workpackService.getWorkpack(workpackParamDto);
		workpack = workpackService.update(workpack);
		ResponseBase<EntityDto> response = new ResponseBase<EntityDto>().setData(new EntityDto(workpack.getId()))
				.setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Workpack workpack = workpackService.findById(id);
		workpackService.delete(workpack);
		return ResponseEntity.ok().build();
	}

}