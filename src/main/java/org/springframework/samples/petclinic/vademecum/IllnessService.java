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

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IllnessService {

	private IllnessRepository illnessRepository;

	@Autowired
	public IllnessService(IllnessRepository illnessRepository) {
		this.illnessRepository = illnessRepository;
	}

	@Transactional(readOnly = true)
	public Iterable<Illness> findAll() throws DataAccessException {
		return illnessRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Illness findIllnessById(int id) throws DataAccessException {
		return illnessRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Illness", "ID", id));
	}

	@Transactional(readOnly = true)
	public List<Illness> findIllnessesBySymptoms(List<String> symptoms) throws DataAccessException {
		return illnessRepository.findIllnessesBySymptoms(symptoms);
	}

	@Transactional
	public Illness saveIllness(Illness illness) throws DataAccessException {
		illnessRepository.save(illness);
		return illness;
	}

	@Transactional
	public Illness updateIllness(Illness illness, int id) throws DataAccessException {
		Illness toUpdate = findIllnessById(id);
		BeanUtils.copyProperties(illness, toUpdate, "id");
		illnessRepository.save(toUpdate);

		return toUpdate;
	}

	@Transactional
	public void deleteIllness(int id) throws DataAccessException {
		Illness toDelete = findIllnessById(id);
		illnessRepository.delete(toDelete);
	}

	@Transactional(readOnly = true)
	public Iterable<Illness> findIllnesses() throws DataAccessException {
		return illnessRepository.findAll();
	}

}
