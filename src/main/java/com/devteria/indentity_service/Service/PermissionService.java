package com.devteria.indentity_service.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devteria.indentity_service.Repository.PermissionRepository;
import com.devteria.indentity_service.Repository.UserRepository;
import com.devteria.indentity_service.dto.request.PermissionRequest;
import com.devteria.indentity_service.dto.response.PermissionResponse;
import com.devteria.indentity_service.entity.Permission;
import com.devteria.indentity_service.mapper.PermissionMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService {
	PermissionRepository permissionRepository;
	PermissionMapper permissionMapper;
	
	public PermissionResponse create(PermissionRequest request) {
		Permission permission = permissionMapper.toPermission(request);
		permission = permissionRepository.save(permission);
		
		return permissionMapper.toPermissionResponse(permission);
	}
	
	public List<PermissionResponse> getAll(){
		var permissions = permissionRepository.findAll();
		return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
		
	}
	
	public void delete(String permission) {
		permissionRepository.deleteById(permission);
	}

}
