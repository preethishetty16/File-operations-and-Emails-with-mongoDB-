package com.preethi.fileops.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidatorErrorResponse {

	private String field;
	
	private String code;
	
	private String message;
}
