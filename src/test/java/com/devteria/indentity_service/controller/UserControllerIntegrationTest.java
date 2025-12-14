package com.devteria.indentity_service.controller;

import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.devteria.indentity_service.Service.UserService;
import com.devteria.indentity_service.dto.request.UserCreationRequest;
import com.devteria.indentity_service.dto.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerIntegrationTest {
	
	@Container
	static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:8.0.43-debian");
	
	@DynamicPropertySource
	static void configureDatasource(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
		registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
		registry.add("spring.datasource.driverClassName", () -> "com.mysql.cj.jdbc.Driver");
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
	}
	
	
	@Autowired
	private MockMvc mockMvc;
	
	
	private UserCreationRequest request;
	private UserResponse userResponse;
	private LocalDate dob;
	
	@BeforeEach
	void initData() {
		
		dob = LocalDate.of(1990,12,1);
		
		request = UserCreationRequest.builder()
				.username("Johnw")
				.firstname("John")
				.lastname("Doe")
				.password("123456789")
				.dob(dob)
				.build();
		
		userResponse = UserResponse.builder()
				.id("4f2f6952c8b1")
				.username("Johnw")
				.firstname("John")
				.lastname("Doe")
				.dob(dob)
				.build();
	}
	
	
	@Test
	void createUser_validRequest_sucess() throws Exception {
		//GIVEN
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		String content = objectMapper.writeValueAsString(request);
		
//		WHEN
		var response = mockMvc.perform(MockMvcRequestBuilders
				.post("/users")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(content))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
				.andExpect(MockMvcResultMatchers.jsonPath("result.username").value("Johnw"))
				.andExpect(MockMvcResultMatchers.jsonPath("result.firstname").value("John"))
				.andExpect(MockMvcResultMatchers.jsonPath("result.lastname").value("Doe"));
		log.info("Result: {}", response.andReturn().getResponse().getContentAsString());
	}
	
}
