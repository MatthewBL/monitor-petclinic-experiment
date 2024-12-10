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
package org.springframework.samples.petclinic.illness;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SymptomService {

	private SymptomRepository symptomRepository;

	@Autowired
	public SymptomService(SymptomRepository symptomRepository) {
		this.symptomRepository = symptomRepository;
	}

	@Transactional(readOnly = true)
	public Iterable<Symptom> findAll() throws DataAccessException {
		return symptomRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Symptom findSymptomById(int id) throws DataAccessException {
		return symptomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Symptom", "ID", id));
	}

	@Transactional
	public Symptom saveSymptom(Symptom symptom) throws DataAccessException {
		symptomRepository.save(symptom);
		return symptom;
	}

	@Transactional
	public Symptom updateSymptom(Symptom symptom, int id) throws DataAccessException {
		Symptom toUpdate = findSymptomById(id);
		BeanUtils.copyProperties(symptom, toUpdate, "id");
		symptomRepository.save(toUpdate);

		return toUpdate;
	}

	@Transactional
	public void deleteSymptom(int id) throws DataAccessException {
		Symptom toDelete = findSymptomById(id);
		symptomRepository.delete(toDelete);
	}

	@Transactional(readOnly = true)
	public Iterable<Symptom> findSymptoms() throws DataAccessException {
		return symptomRepository.findAll();
	}

}
