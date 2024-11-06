package com.preethi.fileops.service;

import org.bson.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
	
	private String id; // Object ID as a string
    private String data;
    
    private Document docs;

}
