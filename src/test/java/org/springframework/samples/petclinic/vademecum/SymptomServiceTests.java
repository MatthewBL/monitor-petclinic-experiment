/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vademecum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.pet.exceptions.DuplicatedPetNameException;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.transaction.annotation.Transactional;

//@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@SpringBootTest
@AutoConfigureTestDatabase
class SymptomServiceTests {

	private SymptomService symptomService;
	
	@Autowired
	public SymptomServiceTests(SymptomService symptomService) {
		this.symptomService = symptomService;
	}

	@Test
	void shouldFindSymptoms() {
		Collection<Symptom> symptoms = (Collection<Symptom>) this.symptomService.findAll();

		Symptom symptom = EntityUtils.getById(symptoms, Symptom.class, 1);
		assertEquals("Coughing", symptom.getName());
	}

	@Test
	void shouldFindSingleSymptom() {
		Symptom symptom = this.symptomService.findSymptomById(1);
		assertThat(symptom.getName()).startsWith("fever");
	}

	@Test
	void shouldNotFindSingleSymptomWithBadID() {
		assertThrows(ResourceNotFoundException.class, () -> this.symptomService.findSymptomById(-1));
	}

	@Test
	@Transactional
	void shouldUpdateSymptom() {
		Symptom symptom = this.symptomService.findSymptomById(1);
		symptom.setName("Update");
		symptomService.updateSymptom(symptom, 1);
		symptom = this.symptomService.findSymptomById(1);
		assertEquals("Update", symptom.getName());
	}

	@Test
	@Transactional
	void shouldInsertSymptom() {
		Collection<Symptom> symptoms = (Collection<Symptom>) this.symptomService.findAll();
		int found = symptoms.size();

		Symptom symptom = new Symptom();
		symptom.setName("NewSymptom");
		

		this.symptomService.saveSymptom(symptom);
		assertNotEquals(0, symptom.getId().longValue());

		symptoms = (Collection<Symptom>) this.symptomService.findAll();
		assertEquals(found + 1, symptoms.size());
	}

	@Test
	@Transactional
	void shouldDeleteSymptom() throws DataAccessException, DuplicatedPetNameException {
		Integer firstCount = ((Collection<Symptom>) this.symptomService.findAll()).size();
		Symptom symptom = new Symptom();
		symptom.setName("Delete");
		this.symptomService.saveSymptom(symptom);

		Integer secondCount = ((Collection<Symptom>) this.symptomService.findAll()).size();
		assertEquals(firstCount + 1, secondCount);
		symptomService.deleteSymptom(symptom.getId());
		Integer lastCount = ((Collection<Symptom>) this.symptomService.findAll()).size();
		assertEquals(firstCount, lastCount);
	}

}
