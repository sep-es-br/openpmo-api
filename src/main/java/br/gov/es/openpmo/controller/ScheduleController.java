package br.gov.es.openpmo.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.schedule.GroupStepDto;
import br.gov.es.openpmo.dto.schedule.ScheduleDto;
import br.gov.es.openpmo.dto.schedule.ScheduleParamDto;
import br.gov.es.openpmo.dto.schedule.StepDto;
import br.gov.es.openpmo.dto.schedule.StepParamDto;
import br.gov.es.openpmo.model.Schedule;
import br.gov.es.openpmo.model.Step;
import br.gov.es.openpmo.service.ScheduleService;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/schedules")
public class ScheduleController {

	private final ScheduleService scheduleService;
	private final ModelMapper modelMapper;

	@Autowired
	public ScheduleController(ScheduleService scheduleService, ModelMapper modelMapper) {
		this.scheduleService = scheduleService;
		this.modelMapper = modelMapper;
	}

	@GetMapping
	public ResponseEntity<ResponseBase<List<ScheduleDto>>> indexBase(@RequestParam(value = "id-workpack") Long idWorkpack) {
		List<ScheduleDto> schedules = scheduleService.findAll(idWorkpack).stream().map(o -> {
				ScheduleDto scheduleDto = modelMapper.map(o, ScheduleDto.class);
				if (o.getSteps() != null) {
					scheduleDto.setGroupStep(getGroupStep(o.getSteps()));
				}
				scheduleDto.setIdWorkpack(o.getWorkpack().getId());
				return scheduleDto;
			}).collect(
			Collectors.toList());
		if (schedules.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		ResponseBase<List<ScheduleDto>> response = new ResponseBase<List<ScheduleDto>>().setData(schedules).setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	private List<GroupStepDto> getGroupStep(Set<Step> steps) {
		List<GroupStepDto> groups = new ArrayList<>(0);
		if (steps != null) {
			Map<Integer, List<Step>> map = new HashMap<>();
			steps.forEach(s -> {
				Integer key = s.getPeriodFromStart().getYear();
				map.computeIfAbsent(key, k -> new ArrayList<>());
				map.get(key).add(s);
			});
			map.keySet().forEach(k -> {
				GroupStepDto group = new GroupStepDto();
				group.setYear(k);
				map.get(k).forEach(s -> group.getSteps().add(modelMapper.map(s, StepDto.class)));
				group.getSteps().sort(Comparator.comparing(StepDto::getPeriodFromStart));
				groups.add(group);
			});
			groups.sort(Comparator.comparing(GroupStepDto::getYear));
		}
		return groups;
	}

	@GetMapping("{id}")
	public ResponseEntity<ResponseBase<ScheduleDto>> findById(@PathVariable Long id) {
		Schedule schedule =  scheduleService.findById(id);
		ScheduleDto scheduleDto = modelMapper.map(schedule, ScheduleDto.class);
		if (schedule.getSteps() != null) {
			scheduleDto.setGroupStep(getGroupStep(schedule.getSteps()));
		}
		scheduleDto.setIdWorkpack(schedule.getWorkpack().getId());
		ResponseBase<ScheduleDto> response = new ResponseBase<ScheduleDto>().setData(scheduleDto).setSuccess(true);
		return ResponseEntity.status(200).body(response);
	}

	@PostMapping
	public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody ScheduleParamDto scheduleParamDto) {
		Schedule schedule = scheduleService.getSchedule(scheduleParamDto);
		schedule = scheduleService.save(schedule);
		ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(schedule.getId())).setSuccess(true);
		return ResponseEntity.status(200).body(entity);
	}

	@PutMapping
	public ResponseEntity<ResponseBase<EntityDto>> update(@RequestBody @Valid StepParamDto stepParamDto) {
		Step step = scheduleService.getStep(stepParamDto);
		step = scheduleService.update(step);
		ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(new EntityDto(step.getId())).setSuccess(true);
		return ResponseEntity.status(200).body(entity);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Schedule schedule = scheduleService.findById(id);
		scheduleService.delete(schedule);
		return ResponseEntity.ok().build();
	}

}
