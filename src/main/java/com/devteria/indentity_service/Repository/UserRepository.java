package com.devteria.indentity_service.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devteria.indentity_service.entity.User;
import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
	boolean existsByUsername(String username);	
	Optional<User> findByUsername(String username);
}
