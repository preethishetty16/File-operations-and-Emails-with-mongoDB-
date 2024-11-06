package com.preethi.fileops.filescontroller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.preethi.fileops.resource.FileProessResponse;
import com.preethi.fileops.resource.Response;
import com.preethi.fileops.resource.ServiceMessage;
import com.preethi.fileops.resource.ValidatorErrorResponse;
import com.preethi.fileops.service.DocumentDTO;
import com.preethi.fileops.service.FileUploadService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping(value = "/v1/abc")
public class FileController {

	@Autowired
	FileUploadService fileService;

	@PostMapping(value = "/upload")
	public ResponseEntity<Response<FileProessResponse>> createEmployee(
			@RequestHeader(name = "Country", required = true) String country,
			@RequestHeader(name = "UserId", required = true) String userId,
			@RequestParam(name = "file") MultipartFile file) throws IOException {

		FileProessResponse response = fileService.processFile(country, userId, file);
		ServiceMessage serviceMessage;
		HttpStatus httpStatus;
		if (null == response) {
			serviceMessage = ServiceMessage.createDuplicateDataServiceMessage();
			httpStatus = HttpStatus.CONFLICT;
			serviceMessage.getErrors().add(new ValidatorErrorResponse(null, "CODE", "EMPLOYEE EXISTS"));
		} else {
			serviceMessage = ServiceMessage.successMessageForCreated();
			httpStatus = HttpStatus.CREATED;
			serviceMessage.getMessages().add(new ValidatorErrorResponse(null, "CODE", "EMPLOYEE CREATED"));
		}
		return new ResponseEntity<>(Response.createResponse(serviceMessage, response), httpStatus);

	}

	@PostMapping(value = "/download")
	public ResponseEntity<Response<?>> getAllEmployees(@RequestHeader(name = "Country", required = true) String country,
			@RequestHeader(name = "UserId", required = true) String userId) throws IOException {

		List<DocumentDTO> response = fileService.downloadFile(country, userId);

		ServiceMessage serviceMessage;
		HttpStatus httpStatus;
		if (null == response) {
			serviceMessage = ServiceMessage.createDuplicateDataServiceMessage();
			httpStatus = HttpStatus.NOT_FOUND;
			serviceMessage.getErrors().add(new ValidatorErrorResponse(null, "CODE", "EMPLOYEE EXISTS"));
		} else {
			serviceMessage = ServiceMessage.successMessageForCreated();
			httpStatus = HttpStatus.CREATED;
			serviceMessage.getMessages().add(new ValidatorErrorResponse(null, "CODE", "EMPLOYEE CREATED"));
		}
		return new ResponseEntity<>(Response.createResponse(serviceMessage, response), httpStatus);

	}

	@PostMapping(value = "/downloadxlsx")
	public ResponseEntity<InputStreamResource> getAllEmployeesInExcel(
			@RequestHeader(name = "Country", required = true) String country,
			@RequestHeader(name = "UserId", required = true) String userId) throws IOException {
		try {
			byte[] excelFile = fileService.downloadFileXlsx(country, userId);

			InputStreamResource resource = new InputStreamResource(new java.io.ByteArrayInputStream(excelFile));

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=documents.xlsx");

			return ResponseEntity.ok().headers(headers).contentLength(excelFile.length)
					.contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM).body(resource);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}

	}

	@PostMapping(value="/email")
	public ResponseEntity<Response<?>> sendEmailWithDeatils(
			@RequestHeader(name="Country" , required= true) String country,
			@RequestHeader(name ="UserId", required = true) String userId,
			@RequestHeader(name ="fromEmail", required = true) String fromEmail,
			@RequestHeader(name ="toEmail", required = true) String toEmail) throws IOException, MessagingException{
		boolean response= fileService.sendEmailWithAttachment(country, userId,fromEmail,toEmail  );
		
		ServiceMessage serviceMessage;
		HttpStatus httpStatus;
		if(response) {
			serviceMessage = ServiceMessage.successMessageForCreated();
			httpStatus= HttpStatus.OK;
			serviceMessage.getMessages().add(new ValidatorErrorResponse(null,"CODE","Email sent!"));
		}else {
			serviceMessage = ServiceMessage.createBadRequestServiceMessage();
			httpStatus= HttpStatus.BAD_REQUEST;
			serviceMessage.getErrors().add(new ValidatorErrorResponse(null, "CODE","Sorry"));
		}
		return new ResponseEntity<>(Response.createResponse(serviceMessage,null), httpStatus);
	}
}
