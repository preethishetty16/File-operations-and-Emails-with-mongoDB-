package com.preethi.fileops.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.preethi.fileops.resource.FileProessResponse;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class FileUploadService {

	@Autowired
	ExcelService service;
	
	private MongoCollection<Document> collection;
	
	@Autowired
    private JavaMailSender emailSender;

	public FileProessResponse processFile(String country, String userId, MultipartFile file) throws IOException {
		FileProessResponse fileProessResponse = new FileProessResponse();
		Future<?> response = service.readXlsxFile(file);
		fileProessResponse.setIsacknowledged(response);
		return fileProessResponse;
	}

	public List<DocumentDTO> downloadFile(String country, String userId) {

		MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
		MongoDatabase database = mongoClient.getDatabase("ABC");
		this.collection = database.getCollection("filedata");
		
		
		List<DocumentDTO> employees = new ArrayList<>();
        for (Document doc : collection.find()) {
        	DocumentDTO abc= new DocumentDTO();
        	abc.setId(doc.getObjectId("_id").toHexString());
        	abc.setData(doc.toJson());
        	abc.setDocs(doc);
        	employees.add(abc);
        }
		return employees;
	}
	
	
	public byte[] downloadFileXlsx(String country, String userId) throws IOException, MessagingException {

		MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
		MongoDatabase database = mongoClient.getDatabase("ABC");
		this.collection = database.getCollection("filedata");
		
		
		List<DocumentDTO> employees = new ArrayList<>();
        for (Document doc : collection.find()) {
        	DocumentDTO abc= new DocumentDTO();
        	abc.setId(doc.getObjectId("_id").toHexString());
        	abc.setData(doc.toJson());
        	abc.setDocs(doc);
        	employees.add(abc);
        }
        byte[] excelFile=convertJsonToExcel(employees);
        
//        sendEmailWithAttachment(excelFile);
		return excelFile;
	}

	private boolean sendEmailWithAttachment(byte[] excelFile, String fromEmail, String toEmail) throws MessagingException {
		// Create a MimeMessage for sending email with attachment
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);		
        
        helper.setTo(toEmail);
        helper.setSubject("Thanks for downloading ...");
        helper.setText("Hello Thanks for downloading the data ");
        helper.setFrom(fromEmail);
        
        ByteArrayResource resource = new ByteArrayResource(excelFile);
        helper.addAttachment("documents.xlsx", resource);
        
        try {
            // Send the email
            emailSender.send(mimeMessage);
            System.out.println("Email sent successfully with attachment.");
            return true;
        } catch (MailException e) {
            System.out.println("Error sending email: " + e.getMessage());
            return false;
        }
	}

	private byte[] convertJsonToExcel(List<DocumentDTO> employees) throws IOException {
		
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Documents");
        
        if (!employees.isEmpty()) {
        	Row headerRow = sheet.createRow(0);
        	Set<String> headerSet= new HashSet<>();
        	
        	//iterate and get all column names
        	for (DocumentDTO doc :employees) {
        			headerSet.addAll(doc.getDocs().keySet());
        	}
        	
        	//Sorting - not necessary
            List<String> headers = new ArrayList<>(headerSet);
            Collections.sort(headers);
            
            //Add headers into excel
            int cellIndex = 0;
            for (String header : headers) {
                Cell headerCell = headerRow.createCell(cellIndex++);
                headerCell.setCellValue(header);
            }
            
            int rowIndex=1;
            for (DocumentDTO doc : employees) {
            	Row dataRow= sheet.createRow(rowIndex++);
            	cellIndex=0;
            	for(String header :headers) {
            		Cell dataCell=dataRow.createCell(cellIndex++);
            		Object value= doc.getDocs().get(header);
            		if(value != null ) {
            			dataCell.setCellValue(value.toString());
            		}
            		else {
            			dataCell.setCellValue("");
            		}
            	}
            }
        }
        
        ByteArrayOutputStream output= new ByteArrayOutputStream();
        workbook.write(output);
        workbook.close();
        
        return output.toByteArray(); 
	}

	public boolean sendEmailWithAttachment(String country, String userId, String fromEmail, String toEmail) throws IOException, MessagingException {
		byte[] excelFile= downloadFileXlsx(country, userId);
        return sendEmailWithAttachment(excelFile, fromEmail, toEmail);
	}
}
