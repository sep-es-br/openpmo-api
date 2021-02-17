package br.gov.es.openpmo.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseWorkpackModel;
import br.gov.es.openpmo.dto.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelParamDto;
import br.gov.es.openpmo.model.PropertyModel;
import br.gov.es.openpmo.model.WorkpackModel;
import br.gov.es.openpmo.service.WorkpackModelService;

@RestController
@CrossOrigin
@RequestMapping(value = "/workpack-model")
public class WorkpackModelController {

	private final WorkpackModelService workpackModelService;

	@Autowired
	public WorkpackModelController(WorkpackModelService workpackModelService) {
		this.workpackModelService = workpackModelService;
	}

	@GetMapping
	public ResponseEntity<ResponseBaseWorkpackModel> indexBase(
			@RequestParam(value = "id-plan-model") Long idPlanModel) {
		List<WorkpackModelDto> worList = new ArrayList<>();
		workpackModelService.findAll(idPlanModel).forEach(w ->
			worList.add(workpackModelService.getWorkpackModelDto(w))
		);

		if (worList.isEmpty())

		{
			return ResponseEntity.noContent().build();
		}
		ResponseBaseWorkpackModel base = new ResponseBaseWorkpackModel().setData(worList).setMessage("Sucesso")
				.setSuccess(true);
		return ResponseEntity.status(200).body(base);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseBaseWorkpackModelDetail> find(@PathVariable Long id) {
		WorkpackModel workpackModel = workpackModelService.findById(id);
		if (!CollectionUtils.isEmpty(workpackModel.getProperties())) {
			workpackModel.setProperties(new LinkedHashSet<>(workpackModel.getProperties().stream().sorted(Comparator.comparing(PropertyModel::getSortIndex)).collect(
				Collectors.toCollection(LinkedHashSet::new))));
		}
		WorkpackModelDetailDto modelDetailDto = workpackModelService.getWorkpackModelDetailDto(workpackModel);
		ResponseBaseWorkpackModelDetail base = new ResponseBaseWorkpackModelDetail().setData(modelDetailDto)
				.setMessage("Sucesso").setSuccess(true);
		return ResponseEntity.status(200).body(base);
	}

	@PostMapping
	public ResponseEntity<ResponseBase<EntityDto>> save(
			@RequestBody @Valid WorkpackModelParamDto workpackModelParamDto) {
		WorkpackModel workpackModel = workpackModelService.getWorkpackModel(workpackModelParamDto);
		workpackModel = workpackModelService.save(workpackModel);
		ResponseBase<EntityDto> response = new ResponseBase<EntityDto>().setData(new EntityDto(workpackModel.getId()))
				.setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@PutMapping
	public ResponseEntity<ResponseBase<EntityDto>> update(
			@RequestBody @Valid WorkpackModelParamDto workpackModelParamDto) {
		WorkpackModel workpackModel = workpackModelService.getWorkpackModel(workpackModelParamDto);
		workpackModel = workpackModelService.update(workpackModel);
		ResponseBase<EntityDto> response = new ResponseBase<EntityDto>().setData(new EntityDto(workpackModel.getId()))
				.setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		WorkpackModel workpackModel = workpackModelService.findById(id);
		workpackModelService.delete(workpackModel);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}/parent-project")
	public ResponseEntity<ResponseBase<Boolean>> parentProject(@PathVariable Long id) {
		Boolean isParentProject = workpackModelService.isParentProject(id);
		ResponseBase<Boolean> response = new ResponseBase<Boolean>().setData(isParentProject).setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@GetMapping("/can-delete-property/{id}")
	public ResponseEntity<ResponseBase<Boolean>> canDelete(@PathVariable Long id) {
		Boolean canDelete = workpackModelService.isCanDeleteProperty(id);
		ResponseBase<Boolean> response = new ResponseBase<Boolean>().setData(canDelete).setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@GetMapping("/delete-property/{id}")
	public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
		workpackModelService.deleteProperty(id);
		return ResponseEntity.ok().build();
	}

}
