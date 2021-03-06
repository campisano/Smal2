package org.smal2.test.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smal2.common.MD5Generator;
import org.smal2.domain.entity.Laboratory;
import org.smal2.domain.entity.User;
import org.smal2.service.auth.LoginUserRequest;
import org.smal2.service.laboratory.RegisterLaboratoryRequest;
import org.smal2.test.testutil.ABaseTest;

public class RegisterLaboratoryServiceTest extends ABaseTest {

	private String administrator_session_id;

	@Before
	public void before() {
		userRepository.insert(new User("0000", MD5Generator.generate("pass"),
				"Jhon", org.smal2.domain.entity.UserType.ADMINISTRATOR));

		administrator_session_id = authService.loginUser(
				new LoginUserRequest("0000", "pass")).getSession_id();

		laboratoryRepository.insert(new Laboratory("lab01"));
		laboratoryRepository.insert(new Laboratory("lab02"));
		laboratoryRepository.insert(new Laboratory("lab03"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerNullLaboratoryNameMustThrowException() {
		// Act
		laboratoryService.registerLaboratory(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerEmptyLaboratoryNameMustThrowException() {
		// Act
		laboratoryService.registerLaboratory(new RegisterLaboratoryRequest(
				administrator_session_id, ""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerExistentLaboratoryNameMustThrowException() {
		// Act
		laboratoryService.registerLaboratory(new RegisterLaboratoryRequest(
				administrator_session_id, "lab01"));
	}

	@Test
	public void registerNewLaboratoryNameMustSaveLaboratory() {
		// Arrange
		String name = "lab04";

		// Act
		laboratoryService.registerLaboratory(new RegisterLaboratoryRequest(
				administrator_session_id, name));

		// Assert
		Assert.assertEquals(name, laboratoryRepository.get(name).getName());
		Assert.assertEquals(4, laboratoryRepository.listAll().size());
	}
}
