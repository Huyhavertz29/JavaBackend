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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.devteria.indentity_service.Service.UserService;
import com.devteria.indentity_service.dto.request.UserCreationRequest;
import com.devteria.indentity_service.dto.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UserService userService;
	
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
		when(userService.createUser(ArgumentMatchers.any()))
		.thenReturn(userResponse);
		//WHEN
		mockMvc.perform(MockMvcRequestBuilders
				.post("/users")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(content))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
				.andExpect(MockMvcResultMatchers.jsonPath("result.id").value("4f2f6952c8b1")
				
		);
		
		//THEN
	}
	@Test
	void createUser_usernameInvalid_fail() throws Exception {
		//GIVEN
		
		request.setUsername("john");
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		String content = objectMapper.writeValueAsString(request);
		
		
//		when(userService.createUser(ArgumentMatchers.any()))
//		.thenReturn(userResponse);
		
		
		//WHEN,THEN
		mockMvc.perform(MockMvcRequestBuilders
				.post("/users")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(content))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1003))
				.andExpect(MockMvcResultMatchers.jsonPath("message").value("Username must be at least 5 characters")
				
		);
		
		
	}
	
}
