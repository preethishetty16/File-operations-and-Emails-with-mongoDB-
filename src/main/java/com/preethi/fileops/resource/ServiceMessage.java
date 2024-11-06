package com.preethi.fileops.resource;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMessage {

	private String code;

	private String type;

	private String description;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<ValidatorErrorResponse> errors;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<ValidatorErrorResponse> messages;

	public ServiceMessage(String code, String type, String description) {
		this(code, type, description, new ArrayList<ValidatorErrorResponse>(), new ArrayList<ValidatorErrorResponse>());
	}

	public static ServiceMessage createDuplicateDataServiceMessage() {
		return new ServiceMessage("409", "Duplicate data", "Already exists", new ArrayList<ValidatorErrorResponse>(),
				null);
	}

	public static ServiceMessage successMessageForCreated() {
		return new ServiceMessage("201", "Success", "Employee Created", null, new ArrayList<ValidatorErrorResponse>());
	}
	
	public static ServiceMessage createBadRequestServiceMessage() {
		return new ServiceMessage("400", "Bad request", "please check the input", new ArrayList<ValidatorErrorResponse>(),
				null);
	}

}
