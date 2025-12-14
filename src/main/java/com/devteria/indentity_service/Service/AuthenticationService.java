package com.devteria.indentity_service.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.apache.logging.log4j.CloseableThreadContext.Instance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyWebServer;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devteria.indentity_service.Repository.InvalidatedTokenRepository;
import com.devteria.indentity_service.Repository.UserRepository;
import com.devteria.indentity_service.dto.request.AuthenticationRequest;
import com.devteria.indentity_service.dto.request.IntrospectRequest;
import com.devteria.indentity_service.dto.request.LogoutRequest;
import com.devteria.indentity_service.dto.request.RefreshRequest;
import com.devteria.indentity_service.dto.response.AuthenticationResponse;
import com.devteria.indentity_service.dto.response.IntrospectResponse;
import com.devteria.indentity_service.entity.InvalidatedToken;
import com.devteria.indentity_service.entity.User;
import com.devteria.indentity_service.exception.AppException;
import com.devteria.indentity_service.exception.ErrorCode;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
	UserRepository userRepository;
	InvalidatedTokenRepository invalidatedTokenRepository;
	@NonFinal
	@Value("${jwt.signerKey}")
	protected String SIGNER_KEY;
	
	@NonFinal
	@Value("${jwt.valid-duration}")
	protected long VALID_DURATION;
	
	@NonFinal
	@Value("${jwt.refreshable-duration}")
	protected long REFRESHABLE_DURATION;

	public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
		var token = request.getToken();

		boolean isValid = true;
		try {
			verifyToken(token,false);
		} catch (AppException e) {
			isValid = false;
		}
		return IntrospectResponse.builder()
				.valid(isValid)
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		var user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

		if (!authenticated)
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		var token = generateToken(user);
		return AuthenticationResponse.builder().token(token).authenticated(true).build();
	}

	public void Logout(LogoutRequest request) throws JOSEException, ParseException {
		
		try {
			var signToken = verifyToken(request.getToken(),true);
			
			String jit = signToken.getJWTClaimsSet().getJWTID();
			Date expirytime = signToken.getJWTClaimsSet().getExpirationTime();
			
			InvalidatedToken invalidatedToken = InvalidatedToken.builder()
					.id(jit)
					.expirytime(expirytime)
					.build();
			
			invalidatedTokenRepository.save(invalidatedToken);
		} catch (AppException e) {
			log.info("Token already expired");
		}
		
	}

	private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

		SignedJWT signedJWT = SignedJWT.parse(token);

		Date expityTime = (isRefresh) 
				? new Date(signedJWT
						.getJWTClaimsSet()
						.getIssueTime()
						.toInstant()
						.plus(REFRESHABLE_DURATION,ChronoUnit.SECONDS)
						.toEpochMilli())
				: signedJWT.getJWTClaimsSet().getExpirationTime();
		
		var verified = signedJWT.verify(verifier);

		if (!(verified && expityTime.after(new Date())))
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		
		if(invalidatedTokenRepository
				.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		return signedJWT;

	}
	
	public AuthenticationResponse refreshToken(RefreshRequest request) 
			throws JOSEException, ParseException {
		var signJWT = verifyToken(request.getToken(),true);
		
		var jit = signJWT.getJWTClaimsSet().getJWTID();
		var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();
		

		InvalidatedToken invalidatedToken = InvalidatedToken.builder()
				.id(jit)
				.expirytime(expiryTime)
				.build();
		
		invalidatedTokenRepository.save(invalidatedToken);
		
		var username = signJWT.getJWTClaimsSet().getSubject();
		
		var user = userRepository.findByUsername(username).orElseThrow(
				()-> new AppException(ErrorCode.UNAUTHENTICATED));
		
		var token = generateToken(user);
		return AuthenticationResponse.builder().token(token).authenticated(true).build();
		
		
	}
	private String generateToken(User user) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(user.getUsername()).issuer("HuyHavertz.com")
				.issueTime(new Date())
				.expirationTime(new Date(Instant.now().plus(VALID_DURATION,ChronoUnit.SECONDS).toEpochMilli()))
				.claim("scope", buildScope(user))
				.jwtID(UUID.randomUUID().toString())
				.build();
 
		Payload payload = new Payload(jwtClaimsSet.toJSONObject());

		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			log.error("Cannot create token", e);
			throw new RuntimeException(e);
		}

	}

	private String buildScope(User user) {
		StringJoiner stringJoiner = new StringJoiner(" ");
		if (!org.springframework.util.CollectionUtils.isEmpty(user.getRoles()))
			user.getRoles().forEach(role -> {
				stringJoiner.add("ROLE_" + role.getName());
				if (!org.springframework.util.CollectionUtils.isEmpty(role.getPermissions()))
					role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
			});
		return stringJoiner.toString();
	}
}
