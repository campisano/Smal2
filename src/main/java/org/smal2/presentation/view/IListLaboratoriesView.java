package org.smal2.presentation.view;

import org.smal2.service.laboratory.ListLaboratoriesResponse;

public interface IListLaboratoriesView {

	ListLaboratoriesResponse getResponse();

	void setResponse(ListLaboratoriesResponse response);
}
