package org.smal2.infrastructure.presentation.view.http;

import org.smal2.infrastructure.presentation.view.http.util.OperationRequest;
import org.smal2.infrastructure.presentation.view.http.util.OperationResponse;
import org.smal2.presentation.presenter.ListTicketsPresenter;
import org.smal2.presentation.view.IListTicketsView;
import org.smal2.presentation.view.basic.ListTicketsViewMock;
import org.smal2.service.ticket.ListTicketsRequest;
import org.smal2.service.ticket.ListTicketsResponse;
import org.smal2.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@RequestMapping("/ticket")
public class ListTicketsViewJSON {
	@Autowired
	private TicketService ticketService;

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public OperationResponse<ListTicketsResponse> listTickets(
			@RequestBody OperationRequest<ListTicketsRequest> request) {
		OperationResponse<ListTicketsResponse> response = new OperationResponse<ListTicketsResponse>();

		try {
			// [CMP] spring controllers are singleton (as common servlet)
			// so we can't implements IView because his properties are shared
			IListTicketsView view = new ListTicketsViewMock();

			view.setRequest(request.getRequest());
			new ListTicketsPresenter(view, ticketService);
			view.getCommand().execute();

			if (view.getError() != null) {
				response.setError(view.getError());
			} else {
				response.setResponse(view.getResponse());
			}
		} catch (Exception ex) {
			response.setError("Unexpected error:\n" + ex.getMessage());
		}

		return response;
	}
}
