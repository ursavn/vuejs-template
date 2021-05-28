package com.ursa.tools.amazon.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LineTokenResponse {
    private int status;
    private String message;
    
    @JsonProperty("access_token")
    private String token;
}
