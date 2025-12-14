package com.devteria.indentity_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.devteria.indentity_service.Repository.UserRepository;
import com.devteria.indentity_service.Service.UserService;
import com.devteria.indentity_service.dto.request.UserCreationRequest;
import com.devteria.indentity_service.dto.response.UserResponse;
import com.devteria.indentity_service.entity.User;
import com.devteria.indentity_service.exception.AppException;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {

	@Autowired
	private UserService userService;
	
	@MockBean
	private UserRepository userRepository;

	
	private UserCreationRequest request;
	private UserResponse userResponse;
	private User user;
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
		
		user = User.builder()
				.id("4f2f6952c8b1")
				.username("Johnw")
				.firstname("John")
				.lastname("Doe")
				.dob(dob).
				build();
	}
	
	@Test
	void createUser_validRequest_success() {
 		//GIVEN
		when(userRepository.existsByUsername(anyString())).thenReturn(false);
		when(userRepository.save(any())).thenReturn(user);
		
		//WHEN
		var response = userService.createUser(request);
		
		//THEN
		assertThat(response.getId()).isEqualTo("4f2f6952c8b1");
		Assertions.assertThat(response.getId()).isEqualTo("4f2f6952c8b1");
		Assertions.assertThat(response.getUsername()).isEqualTo("Johnw");
		
		
	}
	@Test
	void createUser_userExisted_fail() {
 		//GIVEN
		when(userRepository.existsByUsername(anyString())).thenReturn(true);
//		when(userRepository.save(any())).thenReturn(user);
		
		//WHEN
		var exception = assertThrows(AppException.class,
				() -> userService.createUser(request));
		
		
		//THEN
		Assertions.assertThat(exception.getErrorCode()
				.getCode())
				.isEqualTo(1002);
		
		
	}
	@Test
	@WithMockUser(username = "johnw")
	void getMyInfo_valid_success() {
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
		
		var response = userService.getMyInfo();
		
		Assertions.assertThat(response.getUsername()).isEqualTo("Johnw");
		Assertions.assertThat(response.getId()).isEqualTo("4f2f6952c8b1");
			
	}
	@Test
	@WithMockUser(username = "johnw")
	void getMyInfo_userNotFound_error() {
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));
		
		var exception = assertThrows(AppException.class,
				() -> userService.getMyInfo());
		
		Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1005);
	}
	

}
