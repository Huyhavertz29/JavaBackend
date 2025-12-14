package com.devteria.indentity_service.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devteria.indentity_service.Service.PermissionService;
import com.devteria.indentity_service.dto.request.ApiResponse;
import com.devteria.indentity_service.dto.request.PermissionRequest;
import com.devteria.indentity_service.dto.response.PermissionResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

	PermissionService  permissionService;
	
	@PostMapping
	ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request){
		return ApiResponse.<PermissionResponse>builder()
				.result(permissionService.create(request))
				.build();
	}
	
	@GetMapping
	ApiResponse<List<PermissionResponse>> getAll(){
		return ApiResponse.<List<PermissionResponse>>builder()
				.result(permissionService.getAll())
				.build();
	}
	
	@DeleteMapping("/{permission}")
	ApiResponse<Void> delete(@PathVariable String permission){
		permissionService.delete(permission);
		return ApiResponse.<Void>builder().build();
	}
	


}
