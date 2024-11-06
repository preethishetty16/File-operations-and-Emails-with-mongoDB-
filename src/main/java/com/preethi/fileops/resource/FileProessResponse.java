package com.preethi.fileops.resource;

import java.util.concurrent.Future;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileProessResponse {
	
	Future<?> isacknowledged;

}
