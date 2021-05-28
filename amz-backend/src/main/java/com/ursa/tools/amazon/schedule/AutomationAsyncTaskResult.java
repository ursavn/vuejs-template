package com.ursa.tools.amazon.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AutomationAsyncTaskResult {
    private int success;
    private int error;
}
