package com.devteria.indentity_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.devteria.indentity_service.dto.request.UserCreationRequest;
import com.devteria.indentity_service.dto.request.UserUpdateRequest;
import com.devteria.indentity_service.dto.response.UserResponse;
import com.devteria.indentity_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	User toUser(UserCreationRequest request);
	
	@Mapping(target = "roles", ignore= true)
	void updateUser(@MappingTarget User user, UserUpdateRequest request);
	
	UserResponse toUserResponse(User user);
	
} 
