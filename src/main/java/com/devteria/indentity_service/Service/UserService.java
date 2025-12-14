package com.devteria.indentity_service.Service;

import java.util.HashSet;
import java.util.List;

import javax.management.relation.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devteria.indentity_service.Repository.RoleRepository;
import com.devteria.indentity_service.Repository.UserRepository;
import com.devteria.indentity_service.dto.request.UserCreationRequest;
import com.devteria.indentity_service.dto.request.UserUpdateRequest;
import com.devteria.indentity_service.dto.response.UserResponse;
import com.devteria.indentity_service.entity.User;
import com.devteria.indentity_service.enums.Roles;
import com.devteria.indentity_service.exception.AppException;
import com.devteria.indentity_service.exception.ErrorCode;
import com.devteria.indentity_service.mapper.UserMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

	private final PasswordEncoder passwordEncoder;
	UserRepository userRepository;
	UserMapper userMapper;
	RoleRepository roleRepository;

	public UserResponse createUser(UserCreationRequest request) {
//		User user = new User();
		log.info("Huy havertz da den day");
		if (userRepository.existsByUsername(request.getUsername()))
			throw new AppException(ErrorCode.USER_EXISTED);
//			RuntimeException("ErrorCode.USER_EXISTED"); 
		User user = userMapper.toUser(request);
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

//		user.setUsername(request.getUsername());
//		user.setPassword(request.getPassword());
//		user.setFirstname(request.getFirstname());
//		user.setLastname(request.getLastname());
//		user.setDob(request.getDob());
		HashSet<String> roles = new HashSet<>();
		roles.add(Roles.USER.name());
//		user.setRoles(roles);
		return userMapper.toUserResponse(userRepository.save(user));

	}

//	@PreAuthorize("hasRole('ADMIN')")
	@PreAuthorize("hasAuthority('APPROVE_POST')")
	public List<UserResponse> getUsers() {
		log.info("In methot get Users");
		return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();

	}

//	@PostAuthorize("hasRole('ADMIN')")
	@PostAuthorize("returnObject.username == authentication.name")
	public User getUser(String id) {
		log.info("In method get user by id");
		return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
	}

	public UserResponse getMyInfo() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();

		User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		return userMapper.toUserResponse(user);
	}

	public User updateUser(String userId, UserUpdateRequest request) {
		User user = getUser(userId);
		userMapper.updateUser(user, request);	
		
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		var roles = roleRepository.findAllById(request.getRoles());
		user.setRoles(new HashSet<>(roles));
//		user.setPassword(request.getPassword());
//		user.setFirstname(request.getFirstname());
//		user.setLastname(request.getLastname());
//		user.setDob(request.getDob());
		return userRepository.save(user);
		
	}

	public void deleteUser(String userId) {
		userRepository.deleteById(userId);
	}
}
