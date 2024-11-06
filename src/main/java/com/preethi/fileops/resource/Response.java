package com.preethi.fileops.resource;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
	
	private ServiceMessage serviceMessage;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private T extendnedresponse;
	
	private List<?> extendedResponse1;
	
	public static<T> Response<T> createResponse(ServiceMessage message ,T extendnedresponse){
		return new Response<T>(message, extendnedresponse, null);
	}

	public static<T> Response<T> createResponse(ServiceMessage message ,List<?> extendnedresponse){
		return new Response<T>(message, null, extendnedresponse);
	}

}
