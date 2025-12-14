package com.devteria.indentity_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devteria.indentity_service.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}
