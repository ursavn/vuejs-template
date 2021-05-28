package com.ursa.tools.amazon.payload;

import lombok.Data;

@Data
public class LineStatusResponse {
	private int status;
	private String message;
    private String targetType;
    private String target;
}
