package org.springframework.samples.petclinic.vademecum;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for the {@link VademecumController}
 */
@WebMvcTest(controllers = SymptomRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
class SymptomControllerTests {

	private static final int TEST_SYMPTOM_ID = 1;
	private static final String BASE_URL = "/api/v1/vademecum/symptoms";

	@SuppressWarnings("unused")
	@Autowired
	private SymptomRestController symptomRestController;

	@MockBean
	private SymptomService symptomService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Symptom fever;

	@BeforeEach
	void setup() {
		fever = new Symptom();
		fever.setId(TEST_SYMPTOM_ID);
		fever.setName("Fever");
	}

	@Test
	@WithMockUser("admin")
	void shouldFindAll() throws Exception {
		Symptom coughing = new Symptom();
		coughing.setId(2);
		coughing.setName("Coughing");

		Symptom sneezing = new Symptom();
		sneezing.setId(3);
		sneezing.setName("Sneezing");

		when(this.symptomService.findSymptoms()).thenReturn(List.of(fever, coughing, sneezing));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].name").value("Fever"))
				.andExpect(jsonPath("$[?(@.id == 2)].name").value("Coughing"))
				.andExpect(jsonPath("$[?(@.id == 3)].name").value("Sneezing"));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnSymptom() throws Exception {
		when(this.symptomService.findSymptomById(TEST_SYMPTOM_ID)).thenReturn(fever);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_SYMPTOM_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_SYMPTOM_ID))
				.andExpect(jsonPath("$.name").value(fever.getName()));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundSymptom() throws Exception {
		when(this.symptomService.findSymptomById(TEST_SYMPTOM_ID)).thenThrow(ResourceNotFoundException.class);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_SYMPTOM_ID)).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	void shouldCreateSymptom() throws Exception {
		Symptom sp = new Symptom();
		sp.setName("Prueba");

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sp))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser("admin")
	void shouldUpdateSymptom() throws Exception {
		fever.setName("UPDATED");

		when(this.symptomService.findSymptomById(TEST_SYMPTOM_ID)).thenReturn(fever);
		when(this.symptomService.updateSymptom(any(Symptom.class), any(Integer.class))).thenReturn(fever);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_SYMPTOM_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(fever))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("UPDATED"));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundUpdateSymptom() throws Exception {
		fever.setName("UPDATED");

		when(this.symptomService.findSymptomById(TEST_SYMPTOM_ID)).thenThrow(ResourceNotFoundException.class);
		when(this.symptomService.updateSymptom(any(Symptom.class), any(Integer.class))).thenReturn(fever);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_SYMPTOM_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(fever))).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	  void shouldDeleteSymptom() throws Exception {
		when(this.symptomService.findSymptomById(TEST_SYMPTOM_ID)).thenReturn(fever);
		
	    doNothing().when(this.symptomService).deleteSymptom(TEST_SYMPTOM_ID);
	    mockMvc.perform(delete(BASE_URL + "/{id}", TEST_SYMPTOM_ID).with(csrf()))
	         .andExpect(status().isOk());
	  }

}
