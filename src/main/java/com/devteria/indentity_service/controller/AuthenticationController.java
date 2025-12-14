package com.devteria.indentity_service.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devteria.indentity_service.Service.AuthenticationService;
import com.devteria.indentity_service.dto.request.ApiResponse;
import com.devteria.indentity_service.dto.request.AuthenticationRequest;
import com.devteria.indentity_service.dto.request.IntrospectRequest;
import com.devteria.indentity_service.dto.request.LogoutRequest;
import com.devteria.indentity_service.dto.request.RefreshRequest;
import com.devteria.indentity_service.dto.response.AuthenticationResponse;
import com.devteria.indentity_service.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor 
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class AuthenticationController {
	AuthenticationService authenticationService;
	@PostMapping("/token")
	ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
		var result = authenticationService.authenticate(request);
		return ApiResponse.<AuthenticationResponse>builder()
				.result(result)
//				.result(AuthenticationResponse.builder()
//						.authenticated(result)
//						.build())
				.build();
	}
	@PostMapping("/introspect")
	ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
	throws JOSEException, ParseException {
		var result = authenticationService.introspect(request);
		return ApiResponse.<IntrospectResponse>builder()
				.result(result)
				.build();
	}
	@PostMapping("/refresh")
	ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request) 
			throws JOSEException, ParseException{
		var result = authenticationService.refreshToken(request);
		return ApiResponse.<AuthenticationResponse>builder()
				.result(result)
				.build();
	}
	
	@PostMapping("/logout")
	ApiResponse<Void> Logout(@RequestBody LogoutRequest request)
	throws JOSEException, ParseException {
		authenticationService.Logout(request);
		return ApiResponse.<Void>builder()
				.build();
	}
}
