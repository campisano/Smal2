package org.smal2.infrastructure.viewJsonOverHTTP;

import org.smal2.infrastructure.viewJsonOverHTTP.JSONResponse;
import org.smal2.presenter.ListUsersPresenter;
import org.smal2.presenter.view.I_ListUsersView;
import org.smal2.service.UserService;
import org.smal2.service.user.ListUsersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@RequestMapping("/user")
public class ListUsersViewJSON implements I_ListUsersView {
	@Autowired
	private UserService userService;

	private ListUsersResponse users;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public JSONResponse listUsers() {
		try {
			new ListUsersPresenter(this, userService);

			return new JSONResponse(true, users);

		} catch (Exception ex) {
			return new JSONResponse(false, "Error:\n" + ex.getMessage());
		}
	}

	@Override
	public ListUsersResponse getUsers() {
		return users;
	}

	@Override
	public void setUsers(ListUsersResponse users) {
		this.users = users;
	}
}