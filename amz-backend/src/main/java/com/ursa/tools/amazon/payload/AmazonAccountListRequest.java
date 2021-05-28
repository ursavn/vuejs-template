package com.ursa.tools.amazon.payload;

import com.ursa.tools.amazon.constant.enums.AmazonAccountStatus;

import lombok.Data;

@Data
public class AmazonAccountListRequest extends ListRequest {
    private AmazonAccountStatus status;
}
