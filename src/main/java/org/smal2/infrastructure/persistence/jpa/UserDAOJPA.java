package org.smal2.infrastructure.persistence.jpa;

import java.util.List;

import org.smal2.domain.entity.User;
import org.smal2.domain.entity.UserType;
import org.smal2.persistence.IUserDAO;
import org.springframework.stereotype.Component;

@Component
public class UserDAOJPA extends GenericDAOJPA<User> implements IUserDAO {

	@Override
	public User read(String registration) {
		return read(User.class, registration);
	}

	@Override
	public List<User> readAll() {
		return readAll(User.class);
	}

	@Override
	public void delete(String registration) {
		delete(User.class, registration);
	}

	@Override
	public boolean existWithRegistration(String registration) {

		StringBuilder query = new StringBuilder();
		query.append("SELECT e FROM User e WHERE e.registration = ?");
		Object array[] = { registration };

		return super.hasEntity(query.toString(), array);
	}

	@Override
	public boolean existWithEmail(String email) {

		StringBuilder query = new StringBuilder();
		query.append("SELECT e FROM User e WHERE e.email = ?");
		Object array[] = { email };

		return super.hasEntity(query.toString(), array);
	}

	@Override
	public User getBySessionId(String session_id) {

		StringBuilder query = new StringBuilder();
		query.append("SELECT e FROM User e WHERE e.session_id = ?");
		Object array[] = { session_id };

		return super.getEntity(query.toString(), array);
	}

	@Override
	public boolean existWithSessionId(String session_id) {

		StringBuilder query = new StringBuilder();
		query.append("SELECT e FROM User e WHERE e.session_id = ?");
		Object array[] = { session_id };

		return super.hasEntity(query.toString(), array);
	}

	@Override
	public List<User> readAllPrivilegedUsers() {

		StringBuilder query = new StringBuilder();
		query.append("SELECT e FROM User e WHERE e.type = ? OR e.type = ?");
		Object array[] = { UserType.ADMINISTRATOR, UserType.TECHNICIAN };

		return super.getEntities(query.toString(), array);
	}
}