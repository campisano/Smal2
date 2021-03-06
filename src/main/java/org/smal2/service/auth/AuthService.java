package org.smal2.service.auth;

import java.util.Date;

import org.smal2.common.MD5Generator;
import org.smal2.domain.entity.User;
import org.smal2.domain.repository.AuthRepository;
import org.smal2.domain.repository.SubTroubleRepository;
import org.smal2.domain.repository.UserRepository;
import org.smal2.service.user.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthService {

	@Autowired
	private SubTroubleRepository subTroubleRepository;

	@Autowired
	private AuthRepository authRepository;

	@Autowired
	private UserRepository userRepository;

	public LoginUserResponse loginUser(LoginUserRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("Undefined request.");
		}

		if (request.getRegistration() == null
				|| request.getRegistration().equals("")) {
			throw new IllegalArgumentException("Undefined registration.");
		}

		if (request.getPassword() == null || request.getPassword().equals("")) {
			throw new IllegalArgumentException("Undefined password.");
		}

		// local user exists?
		if (!userRepository.existWithRegistration(request.getRegistration())) {

			// remote login
			RemoteLoginResponse response = authRepository
					.getRemoteLoginResponse(request.getRegistration(),
							request.getPassword());

			if (response.getError() != null || response.getToken() == null
					|| response.getToken().trim().isEmpty()) {
				String error = "Cannot authenticate user in remote auth service.";
				error += "\nError: ";
				error += ((response.getError() == null) ? ("Null") : (response
						.getError()));
				throw new IllegalArgumentException(error);
			}

			// local registration
			String md5Pass = MD5Generator.generate(request.getPassword());
			User unprivilegedUser = new User(request.getRegistration(),
					md5Pass, request.getRegistration().concat(" user"),
					org.smal2.domain.entity.UserType.STUDENT);
			unprivilegedUser.setService_token(response.getToken());
			unprivilegedUser.setSession_timestamp(new Date());
			userRepository.insert(unprivilegedUser);
		}

		// local login
		User user = userRepository.get(request.getRegistration());

		String md5Pass = MD5Generator.generate(request.getPassword());

		if (md5Pass == null) {
			throw new IllegalArgumentException(
					"Internal error: cannot generate md5 hash for this password.");
		}

		if (!user.getPassword().equals(md5Pass)) {
			throw new IllegalArgumentException("Invalid username or password");
		}

		// generating session_id
		Date date = new Date();
		String session_id = MD5Generator.generate(String.valueOf(user
				.getRegistration()) + date.toString());
		user.setSession_id(session_id);
		user.setSession_timestamp(date);
		userRepository.save(user);

		UserType type;

		switch (user.getType()) {
		case STUDENT:
			type = UserType.STUDENT;
			break;
		case TECHNICIAN:
			type = UserType.TECHNICIAN;
			break;
		case ADMINISTRATOR:
			type = UserType.ADMINISTRATOR;
			break;

		default:
			throw new IllegalStateException("Invalid user type.");
		}

		return new LoginUserResponse(user.getSession_id(),
				user.getRegistration(), user.getName(), type,
				"User authenticated successfully.");
	}

	public String logoutUser(String session_id) {

		if (session_id == null || session_id.equals("")) {
			throw new IllegalArgumentException("Undefined session identifier.");
		}

		// session identify a logged user
		if (!userRepository.existWithSessionId(session_id)) {
			throw new IllegalArgumentException("Invalid session identifier");
		}

		// logout
		User user = userRepository.getBySessionId(session_id);

		user.setSession_id(null);
		userRepository.save(user);

		return "User deauthenticated successfully.";
	}
}
