package com.devteria.indentity_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devteria.indentity_service.Service.AuthenticationService;
import com.devteria.indentity_service.Service.UserService;
import com.devteria.indentity_service.dto.request.ApiResponse;
import com.devteria.indentity_service.dto.request.UserCreationRequest;
import com.devteria.indentity_service.dto.request.UserUpdateRequest;
import com.devteria.indentity_service.dto.response.UserResponse;
import com.devteria.indentity_service.entity.User;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserService userService;

//	@PostMapping()
//	User createUser(@RequestBody @Valid UserCreationRequest request) {
//		return userService.createUser(request);
//	}

	@PostMapping()
	ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
		log.info("Huy da den day");
		ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
		apiResponse.setResult(userService.createUser(request));
		return apiResponse;
	}

	@GetMapping
	ApiResponse<List<UserResponse>> getUsers() {
		
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		
		log.info("Username: {}",authentication.getName());
		authentication.getAuthorities().forEach(t -> log.info(t.getAuthority()) );
		
		return  ApiResponse.<List<UserResponse>>builder()
				.result(userService.getUsers())
				.build();
	}

//	@GetMapping("/{userId}")
//	User getUser(@PathVariable("userId") String userId) {
//		return userService.getUser(userId);
//	}

	@GetMapping("/{userId}")
	ApiResponse<User> getUser(@PathVariable("userId") String userId) {
		ApiResponse<User> apiResponse = new ApiResponse<>();
		apiResponse.setResult(userService.getUser(userId));
		return apiResponse;
	}
	
	@GetMapping("/myInfo")
	ApiResponse<UserResponse> getMyInfo() {
		return ApiResponse.<UserResponse>builder()
				.result(userService.getMyInfo())
				.build();
	} 

	@PutMapping("/{userId}")
	User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
		return userService.updateUser(userId, request);
	}

	@DeleteMapping("/{userId}")
	String deleteUser(@PathVariable String userId) {
		userService.deleteUser(userId);
		return "User has been deleted";
	}

}
