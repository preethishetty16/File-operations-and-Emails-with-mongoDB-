package com.preethi.fileops.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Service
public class ExcelService {
	
	private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	private final MongoCollection<Document> collection;

	
	public ExcelService() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("ABC");
        this.collection = database.getCollection("filedata");
    }

	public Future<?> readXlsxFile(MultipartFile file) throws IOException {
		Future<?> response=null;
		try (InputStream inputStream = file.getInputStream(); 
			 Workbook workbook = new XSSFWorkbook(inputStream);) {
			
			Sheet sheet = workbook.getSheetAt(0);
			Row headerRow= sheet.getRow(0);
			int numberOfCells= headerRow.getPhysicalNumberOfCells();
			
			for(int rowIndex=1 ; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				Row row= sheet.getRow(rowIndex);
				Map<String, Object> recordMap= new HashMap<>();
				
				for (int colIndex = 0; colIndex < numberOfCells; colIndex++) {
                    String header = headerRow.getCell(colIndex).getStringCellValue();
                    String value = row.getCell(colIndex) != null ? row.getCell(colIndex).toString() : null;
                    recordMap.put(header, value);
                }
				response=executorService.submit(
					() -> {insertRecord(recordMap);}
					);
			}

		}
		return response;
	}

	private void insertRecord(Map<String, Object> recordMap) {
		collection.insertOne(new org.bson.Document(recordMap));
		
	}

}
