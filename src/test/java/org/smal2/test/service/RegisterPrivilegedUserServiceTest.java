package org.smal2.test.service;

import org.smal2.common.MD5Generator;
import org.smal2.domain.entity.User;
import org.smal2.service.auth.LoginUserRequest;
import org.smal2.service.user.RegisterPrivilegedUserRequest;
import org.smal2.service.user.UserType;
import org.smal2.test.testutil.ABaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RegisterPrivilegedUserServiceTest extends ABaseTest {

	private String administrator_session_id;

	@Before
	public void before() {
		userRepository.insert(new User("0", MD5Generator.generate("pass"),
				"Jhon", org.smal2.domain.entity.UserType.ADMINISTRATOR));

		administrator_session_id = authService.loginUser(new LoginUserRequest("0", "pass"))
				.getSession_id();

		userRepository.insert(new User("0001", MD5Generator
				.generate("password1"), "Jhon",
				org.smal2.domain.entity.UserType.STUDENT));
		userRepository.insert(new User("0002", MD5Generator
				.generate("password2"), "Jack",
				org.smal2.domain.entity.UserType.STUDENT));
		userRepository.insert(new User("0003", MD5Generator
				.generate("password3"), "Joe",
				org.smal2.domain.entity.UserType.STUDENT));
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerNullUserMustThrowException() {
		// Act
		userService.registerPrivilegedUser(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerNullUserRegistrationMustThrowException() {
		// Act
		userService.registerPrivilegedUser(new RegisterPrivilegedUserRequest(
				administrator_session_id, null, "password", "Jimmy", "admin@smal.org",
				UserType.ADMINISTRATOR));
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerEmptyUserRegistrationMustThrowException() {
		// Act
		userService.registerPrivilegedUser(new RegisterPrivilegedUserRequest(
				administrator_session_id, "", "password", "Jimmy", "admin@smal.org",
				UserType.ADMINISTRATOR));
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerExistentUserRegistrationMustThrowException() {
		// Act
		userService.registerPrivilegedUser(new RegisterPrivilegedUserRequest(
				administrator_session_id, "0001", "password", "Jimmy", "admin@smal.org",
				UserType.ADMINISTRATOR));
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerStudentServiceMustThrowException() {
		// Act
		userService.registerPrivilegedUser(new RegisterPrivilegedUserRequest(
				administrator_session_id, "0004", "password", "Jimmy", "student@smal.org",
				UserType.STUDENT));
	}

	@Test
	public void registerNewUserNameMustSaveUser() {
		// Arrange
		String registration = "0004";
		String password = "password";
		String name = "Jimmy";
		String email = "admin@smal.org";
		UserType type = UserType.ADMINISTRATOR;

		// Act
		userService.registerPrivilegedUser(new RegisterPrivilegedUserRequest(
				administrator_session_id, registration, password, "Jimmy", email, type));

		// Assert
		User user = userRepository.get(registration);
		String md5Pass = MD5Generator.generate(password);
		Assert.assertEquals(registration, user.getRegistration());
		Assert.assertEquals(md5Pass, user.getPassword());
		Assert.assertEquals(name, user.getName());
		Assert.assertEquals(email, user.getEmail());
		Assert.assertEquals(5, userRepository.listAll().size());
	}
}
