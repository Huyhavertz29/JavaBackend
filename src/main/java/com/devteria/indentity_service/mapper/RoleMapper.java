package com.devteria.indentity_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.devteria.indentity_service.dto.request.PermissionRequest;
import com.devteria.indentity_service.dto.request.RoleRequest;
import com.devteria.indentity_service.dto.request.UserCreationRequest;
import com.devteria.indentity_service.dto.request.UserUpdateRequest;
import com.devteria.indentity_service.dto.response.PermissionResponse;
import com.devteria.indentity_service.dto.response.RoleResponse;
import com.devteria.indentity_service.dto.response.UserResponse;
import com.devteria.indentity_service.entity.Permission;
import com.devteria.indentity_service.entity.Role;
import com.devteria.indentity_service.entity.User;

@Mapper(componentModel = "spring")
public interface RoleMapper {
	@Mapping(target = "permissions", ignore = true)
	Role toRole(RoleRequest request);
	RoleResponse toRoleResponse(Role role);
} 
