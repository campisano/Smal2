package org.smal2.test.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smal2.common.MD5Generator;
import org.smal2.domain.entity.User;
import org.smal2.domain.entity.UserType;
import org.smal2.persistence.IAuthDAO;
import org.smal2.service.auth.LoginUserRequest;
import org.smal2.service.auth.LoginUserResponse;
import org.smal2.test.testutil.ABaseTest;
import org.smal2.test.testutil.mock.AuthDAOMock;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthenticateUserServiceTest extends ABaseTest {

	@Autowired
	private IAuthDAO authDao;

	@Before
	public void before() {
		userRepository.insert(new User("local_registration", MD5Generator
				.generate("local_password"), "local_name", UserType.STUDENT));
		((AuthDAOMock) authDao)
				.create("remote_registration", "remote_password");
	}

	@Test
	public void mustAutowireTestDependencies() {
		Assert.assertNotNull(authDao);
	}

	@Test(expected = IllegalArgumentException.class)
	public void authenticateUserOnNullUsernameMustThrowException() {
		// Act
		authService.loginUser(new LoginUserRequest(null, "1234"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void authenticateUserOnNullPasswordMustThrowException() {
		// Act
		authService.loginUser(new LoginUserRequest("1234", null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void authenticateUserOnEmptyUsernameMustThrowException() {
		// Act
		authService.loginUser(new LoginUserRequest("", "1234"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void authenticateUserOnEmptyPasswordMustThrowException() {
		// Act
		authService.loginUser(new LoginUserRequest("1234", ""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void authenticateUserWithInvalidUsernameMustThrowException() {
		// Act
		authService.loginUser(new LoginUserRequest("1234", "test_login"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void authenticateUserWithInvalidPasswordMustThrowException() {
		// Act
		authService.loginUser(new LoginUserRequest("test_login", "1234"));
	}

	@Test
	public void authenticateExistingUserWithValidAuthMustLogin() {
		// Act
		LoginUserResponse response = authService
				.loginUser(new LoginUserRequest("local_registration",
						"local_password"));

		// Assert
		User user = userRepository.get("local_registration");
		Assert.assertEquals(user.getSession_id(), response.getSession_id());
		Assert.assertEquals("User authenticated successfully.",
				response.getMessage());
	}

	@Test
	public void authenticateUnexistingUserWithValidAuthMustRegisterUserAndLogin() {
		// Act
		LoginUserResponse response = authService
				.loginUser(new LoginUserRequest("remote_registration",
						"remote_password"));

		// Assert
		User user = userRepository.get("remote_registration");
		Assert.assertEquals("remote_registration", user.getRegistration());
		Assert.assertEquals(MD5Generator.generate("remote_password"),
				user.getPassword());
		Assert.assertEquals("remote_registration user", user.getName());
		Assert.assertEquals(user.getSession_id(), response.getSession_id());
		Assert.assertEquals("User authenticated successfully.",
				response.getMessage());
	}

	@Test(expected = IllegalArgumentException.class)
	public void deauthenticateUserOnNullSessionIdMustThrowException() {
		// Act
		authService.logoutUser(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deauthenticateUserOnEmptySessionIdMustThrowException() {
		// Act
		authService.logoutUser("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void deauthenticateUserWithInvalidSessionIdMustThrowException() {
		// Act
		authService.logoutUser("1234");
	}

	@Test
	public void deauthenticateWithValidSessionIdMustLog() {
		// Arrange
		LoginUserResponse loginResponse = authService
				.loginUser(new LoginUserRequest("local_registration",
						"local_password"));

		// Act
		String logoutResponse = authService.logoutUser(loginResponse
				.getSession_id());

		// Assert
		User user = userRepository.get("local_registration");

		Assert.assertEquals(user.getSession_id(), null);
		Assert.assertEquals("User deauthenticated successfully.",
				logoutResponse);
	}
}
