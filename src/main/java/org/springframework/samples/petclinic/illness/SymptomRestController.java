package org.springframework.samples.petclinic.illness;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Symptoms", description = "API for the  management of Symptoms")
@RequestMapping("/api/v1/vets/symptoms")
public class SymptomRestController {

	private final SymptomService symptomService;

	@Autowired
	public SymptomRestController(SymptomService symptomService) {
		this.symptomService = symptomService;
	}

	@GetMapping
	public List<Symptom> findAll() {
		return (List<Symptom>) symptomService.findSymptoms();
	}

	@GetMapping(value = "{symptomId}")
	public ResponseEntity<Symptom> findById(@PathVariable("symptomId") int id) {
		return new ResponseEntity<>(symptomService.findSymptomById(id), HttpStatus.OK);
	}

	@PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Symptom> create(@RequestBody Symptom symptom) {
		Symptom newSymptom = new Symptom();
		BeanUtils.copyProperties(symptom, newSymptom, "id");
		Symptom savedSymptom = this.symptomService.saveSymptom(newSymptom);

		return new ResponseEntity<>(savedSymptom, HttpStatus.CREATED);
	}

	@PutMapping(value = "{symptomId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Symptom> update(@PathVariable("symptomId") int symptomId,
			@RequestBody @Valid Symptom symptom) {
		RestPreconditions.checkNotNull(symptomService.findSymptomById(symptomId), "Symptom", "ID", symptomId);
		return new ResponseEntity<>(this.symptomService.updateSymptom(symptom, symptomId), HttpStatus.OK);
	}

	@DeleteMapping(value = "{symptomId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("symptomId") int id) {
		RestPreconditions.checkNotNull(symptomService.findSymptomById(id), "Symptom", "ID", id);
		symptomService.deleteSymptom(id);
		return new ResponseEntity<>(new MessageResponse("Symptom deleted!"), HttpStatus.OK);
	}

}
