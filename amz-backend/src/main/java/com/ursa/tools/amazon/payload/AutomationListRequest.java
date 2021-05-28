package com.ursa.tools.amazon.payload;

import lombok.Data;

@Data
public class AutomationListRequest extends ListRequest {
   private Boolean status;
}
