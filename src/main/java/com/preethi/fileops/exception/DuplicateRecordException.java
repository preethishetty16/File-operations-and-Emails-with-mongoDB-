package com.preethi.fileops.exception;

import lombok.Data;

@Data
public class DuplicateRecordException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String code;
	
	private final String msgKey;
	
	private final String defaultMsg;

	public DuplicateRecordException(String code, String msgKey, String defaultMsg) {
		this.code = code;
		this.msgKey = msgKey;
		this.defaultMsg = defaultMsg;
	}
	
	
}
