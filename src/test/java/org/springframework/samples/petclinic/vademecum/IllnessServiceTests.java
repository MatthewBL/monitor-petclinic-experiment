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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
class IllnessServiceTests {

	private IllnessService illnessService;
	
	@Autowired
	public IllnessServiceTests(IllnessService illnessService) {
		this.illnessService = illnessService;
	}

	@Test
	void shouldFindIllnesses() {
		Collection<Illness> illnesses = (Collection<Illness>) this.illnessService.findAll();

		Illness illness = EntityUtils.getById(illnesses, Illness.class, 1);
		assertEquals("Illness1", illness.getName());
		assertEquals(2, illness.getSymptoms().size());
		assertEquals("fever", illness.getSymptoms().get(0).getName());
		assertEquals("coughing", illness.getSymptoms().get(1).getName());
	}

	@Test
	void shouldFindSingleIllness() {
		Illness illness = this.illnessService.findIllnessById(0);
		assertThat(illness.getName()).startsWith("Illness0");
		assertEquals(1, illness.getSymptoms().size());
	}

	@Test
	void shouldNotFindSingleIllnessWithBadID() {
		assertThrows(ResourceNotFoundException.class, () -> this.illnessService.findIllnessById(-1));
	}

	@Test
	void shouldFindIllnessesWithSymptoms() {
		List<String> symptoms = Collections.singletonList("Fever");
		Collection<Illness> illnesses = (Collection<Illness>) this.illnessService.findIllnessesBySymptoms(symptoms);

		Illness illness = EntityUtils.getById(illnesses, Illness.class, 0);
		assertEquals("Illness0", illness.getName());
		assertEquals(1, illness.getSymptoms().size());
		assertEquals("fever", illness.getSymptoms().get(0).getName());
	}

	@Test
	@Transactional
	void shouldUpdateIllness() {
		Illness illness = this.illnessService.findIllnessById(1);
		illness.setName("Update");
		illnessService.updateIllness(illness, 1);
		illness = this.illnessService.findIllnessById(1);
		assertEquals("Update", illness.getName());
	}

	@Test
	@Transactional
	void shouldInsertIllness() {
		Collection<Illness> illnesses = (Collection<Illness>) this.illnessService.findAll();
		int found = illnesses.size();

		Illness illness = new Illness();
		illness.setName("NewIllness");
		

		this.illnessService.saveIllness(illness);
		assertNotEquals(0, illness.getId().longValue());

		illnesses = (Collection<Illness>) this.illnessService.findAll();
		assertEquals(found + 1, illnesses.size());
	}

	@Test
	@Transactional
	void shouldDeleteIllness() throws DataAccessException, DuplicatedPetNameException {
		Integer firstCount = ((Collection<Illness>) this.illnessService.findAll()).size();
		Illness illness = new Illness();
		illness.setName("Delete");
		this.illnessService.saveIllness(illness);

		Integer secondCount = ((Collection<Illness>) this.illnessService.findAll()).size();
		assertEquals(firstCount + 1, secondCount);
		illnessService.deleteIllness(illness.getId());
		Integer lastCount = ((Collection<Illness>) this.illnessService.findAll()).size();
		assertEquals(firstCount, lastCount);
	}

}
