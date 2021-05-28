package com.ursa.tools.amazon.payload;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LineNotifyReponse {
    private Integer id;	
    private String targetType;	
	private String target;
	private LocalDateTime createdDate;	
	private LocalDateTime modifiedDate;
}
