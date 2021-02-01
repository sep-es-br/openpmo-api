package br.gov.es.openpmo.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.schedule.StepDto;
import br.gov.es.openpmo.dto.schedule.StepParamDto;
import br.gov.es.openpmo.dto.schedule.StepStoreParamDto;
import br.gov.es.openpmo.model.Step;
import br.gov.es.openpmo.service.ScheduleService;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/schedules/step")
public class StepController {

	private final ScheduleService scheduleService;
	private final ModelMapper modelMapper;

	@Autowired
	public StepController(ScheduleService scheduleService, ModelMapper modelMapper) {
		this.scheduleService = scheduleService;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseBase<StepDto>> findById(@PathVariable Long id) {
		Step  step =  scheduleService.findStepById(id);
		StepDto stepDto = modelMapper.map(step, StepDto.class);
		stepDto.setIdSchedule(step.getSchedule().getId());
		ResponseBase<StepDto> response = new ResponseBase<StepDto>().setData(stepDto).setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@PostMapping
	public ResponseEntity<ResponseBase<EntityDto>> saveStep(@Valid @RequestBody StepStoreParamDto stepStoreParamDto) {
		Step step = scheduleService.getStep(stepStoreParamDto);
		step = scheduleService.save(step);
		scheduleService.addMonthToSchedule(step);
		ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(step.getId())).setSuccess(true);
		return ResponseEntity.status(200).body(entity);
	}

	@PutMapping
	public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid StepParamDto stepParamDto) {
		Step step = scheduleService.getStep(stepParamDto);
		step = scheduleService.update(step);
		ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(step.getId())).setSuccess(true);
		return ResponseEntity.status(200).body(entity);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStep(@PathVariable Long id) {
		Step step = scheduleService.findStepById(id);
		scheduleService.delete(step);
		return ResponseEntity.ok().build();
	}



}
