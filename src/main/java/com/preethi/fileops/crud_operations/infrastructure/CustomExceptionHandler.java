package com.preethi.fileops.crud_operations.infrastructure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.preethi.fileops.exception.DuplicateRecordException;
import com.preethi.fileops.resource.Response;
import com.preethi.fileops.resource.ServiceMessage;
import com.preethi.fileops.resource.ValidatorErrorResponse;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(DuplicateRecordException.class)
	public ResponseEntity<Object> handleDuplicateRecordException(DuplicateRecordException ex) {
		ServiceMessage serviceMessage = ServiceMessage.createDuplicateDataServiceMessage();
		ValidatorErrorResponse valerror = new ValidatorErrorResponse();

		valerror.setCode(ex.getCode());
		valerror.setField(ex.getMsgKey());
		valerror.setMessage(ex.getDefaultMsg());

		serviceMessage.getErrors().add(valerror);
		Response<ErrorResponse> error = Response.createResponse(serviceMessage, null);
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
		ServiceMessage serviceMessage = ServiceMessage.createBadRequestServiceMessage();
		List<ValidatorErrorResponse> valErrorList = new ArrayList<>();

		if (null != ex.getBindingResult() && ex.getBindingResult().getFieldErrors() != null) {
			ex.getBindingResult().getFieldErrors().stream().forEach((error) -> {
				ValidatorErrorResponse valerror = new ValidatorErrorResponse();
				valerror.setCode(error.getCode());
				valerror.setField(error.getField());
				valerror.setMessage(error.getDefaultMessage());
				valErrorList.add(valerror);
			});

		}
		serviceMessage.setErrors(valErrorList);
		Response<ErrorResponse> error = Response.createResponse(serviceMessage, null);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}
