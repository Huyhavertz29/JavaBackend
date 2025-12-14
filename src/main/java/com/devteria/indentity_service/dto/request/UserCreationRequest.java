package com.devteria.indentity_service.dto.request;

import java.time.LocalDate;

import com.devteria.indentity_service.Validator.DobConstraint;
import com.devteria.indentity_service.exception.ErrorCode;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
	@Size(min = 5, message = "USERNAME_INVALID")
	String username;
	
	@Size(min = 9, message = "PASSWORD_INVALID")
	String password;
	String firstname;
	String lastname;
	
	@DobConstraint(min = 18, message = "INVALID_DOB")
	LocalDate dob;
	

}
