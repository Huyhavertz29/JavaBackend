package com.devteria.indentity_service.configuation;

import java.awt.PageAttributes.MediaType;
import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.devteria.indentity_service.dto.request.ApiResponse;
import com.devteria.indentity_service.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class jwtAuthicationionEntryPoint implements AuthenticationEntryPoint{

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
		
		response.setStatus(errorCode.getStatusCode().value());
		response.setContentType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
		
		ApiResponse<?> apiResponse = ApiResponse.builder()
				.code(errorCode.getCode())
				.message(errorCode.getMessage())
				.build();
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
		response.flushBuffer();
		
		
	}




}
