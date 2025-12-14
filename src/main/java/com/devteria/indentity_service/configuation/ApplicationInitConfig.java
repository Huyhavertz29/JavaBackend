package com.devteria.indentity_service.configuation;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devteria.indentity_service.Repository.UserRepository;
import com.devteria.indentity_service.Service.AuthenticationService;
import com.devteria.indentity_service.entity.User;
import com.devteria.indentity_service.enums.Roles;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ApplicationInitConfig {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Bean
	@ConditionalOnProperty(prefix ="spring", 
	value =  "datasource.driverClassName",
	havingValue = "com.mysql.cj.jdbc.Driver"
	)
	ApplicationRunner applicationRunner(UserRepository userRepository) {
		return args -> {
			if(userRepository.findByUsername("admin").isEmpty()) {
				var roles = new HashSet<String>();
				roles.add(Roles.ADMIN.name());
				User user = User.builder()
						.username("admin")
						.password(passwordEncoder.encode("admin"))
//						.roles(roles)
						.build();
				
				userRepository.save(user);
				log.warn("Admin user has been created with default password: admin; please change it");
			}
		};
	}
}
