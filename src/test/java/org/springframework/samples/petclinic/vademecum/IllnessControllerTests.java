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
@WebMvcTest(controllers = IllnessRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
class IllnessControllerTests {

	private static final int TEST_ILLNESS_ID = 1;
	private static final String BASE_URL = "/api/v1/vademecum";

	@SuppressWarnings("unused")
	@Autowired
	private IllnessRestController illnessController;

	@MockBean
	private IllnessService illnessService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Illness illness1;

	@BeforeEach
	void setup() {
		illness1 = new Illness();
		illness1.setId(TEST_ILLNESS_ID);
		illness1.setName("Illness1");
	}

	@Test
	@WithMockUser("admin")
	void shouldFindAll() throws Exception {
		Illness illness2 = new Illness();
		illness2.setId(2);
		illness2.setName("Illness2");

		Illness illness3 = new Illness();
		illness3.setId(3);
		illness3.setName("Illness3");

		when(this.illnessService.findAll()).thenReturn(List.of(illness1, illness2, illness3));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].name").value("Illness1"))
				.andExpect(jsonPath("$[?(@.id == 2)].name").value("Illness2"))
				.andExpect(jsonPath("$[?(@.id == 3)].name").value("Illness3"));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnIllness() throws Exception {
		when(this.illnessService.findIllnessById(TEST_ILLNESS_ID)).thenReturn(illness1);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_ILLNESS_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_ILLNESS_ID))
				.andExpect(jsonPath("$.name").value(illness1.getName()));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundIllness() throws Exception {
		when(this.illnessService.findIllnessById(TEST_ILLNESS_ID)).thenThrow(ResourceNotFoundException.class);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_ILLNESS_ID)).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	void shouldCreateIllness() throws Exception {
		Illness illness = new Illness();
		illness.setName("Prueba");

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(illness))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser("admin")
	void shouldUpdateIllness() throws Exception {
		illness1.setName("UPDATED");

		when(this.illnessService.findIllnessById(TEST_ILLNESS_ID)).thenReturn(illness1);
		when(this.illnessService.updateIllness(any(Illness.class), any(Integer.class))).thenReturn(illness1);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_ILLNESS_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(illness1))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("UPDATED"));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundUpdateIllness() throws Exception {
		illness1.setName("UPDATED");

		when(this.illnessService.findIllnessById(TEST_ILLNESS_ID)).thenThrow(ResourceNotFoundException.class);
		when(this.illnessService.updateIllness(any(Illness.class), any(Integer.class))).thenReturn(illness1);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_ILLNESS_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(illness1))).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	  void shouldDeleteIllness() throws Exception {
		when(this.illnessService.findIllnessById(TEST_ILLNESS_ID)).thenReturn(illness1);
		
	    doNothing().when(this.illnessService).deleteIllness(TEST_ILLNESS_ID);
	    mockMvc.perform(delete(BASE_URL + "/{id}", TEST_ILLNESS_ID).with(csrf()))
	         .andExpect(status().isOk());
	  }

}
