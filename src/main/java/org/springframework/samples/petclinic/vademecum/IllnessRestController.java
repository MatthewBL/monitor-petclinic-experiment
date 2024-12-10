package org.springframework.samples.petclinic.vademecum;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.auth.payload.response.MessageResponse;
import org.springframework.samples.petclinic.util.RestPreconditions;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Illness", description = "API for the  management of Illness")
@RequestMapping("/api/v1/vets/vademecum")
public class IllnessRestController {

	private final IllnessService illnessService;

	@Autowired
	public IllnessRestController(IllnessService illnessService) {
		this.illnessService = illnessService;
	}

	@GetMapping
	public List<Illness> findAll() {
		return (List<Illness>) illnessService.findIllnesses();
	}

	@GetMapping(value = "{illnessId}")
	public ResponseEntity<Illness> findById(@PathVariable("illnessId") int id) {
		return new ResponseEntity<>(illnessService.findIllnessById(id), HttpStatus.OK);
	}

	@PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Illness> create(@RequestBody Illness illness) {
		Illness newIllness = new Illness();
		BeanUtils.copyProperties(illness, newIllness, "id");
		Illness savedIllness = this.illnessService.saveIllness(newIllness);

		return new ResponseEntity<>(savedIllness, HttpStatus.CREATED);
	}

	@PutMapping(value = "{illnessId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Illness> update(@PathVariable("illnessId") int illnessId,
			@RequestBody @Valid Illness illness) {
		RestPreconditions.checkNotNull(illnessService.findIllnessById(illnessId), "Illness", "ID", illnessId);
		return new ResponseEntity<>(this.illnessService.updateIllness(illness, illnessId), HttpStatus.OK);
	}

	@DeleteMapping(value = "{illnessId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("illnessId") int id) {
		RestPreconditions.checkNotNull(illnessService.findIllnessById(id), "Illness", "ID", id);
		illnessService.deleteIllness(id);
		return new ResponseEntity<>(new MessageResponse("Illness deleted!"), HttpStatus.OK);
	}

	@GetMapping("/illnesses")
    public List<Illness> getIllnesses(@RequestParam List<String> symptoms) {
        return illnessService.findIllnessesBySymptoms(symptoms);
    }

}
