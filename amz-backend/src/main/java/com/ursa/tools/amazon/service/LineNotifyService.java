package com.ursa.tools.amazon.service;

import java.util.Optional;

import com.ursa.tools.amazon.model.LineNotify;
import com.ursa.tools.amazon.payload.LineStatusResponse;
import com.ursa.tools.amazon.payload.LineTokenRequest;
import com.ursa.tools.amazon.payload.LineTokenResponse;

public interface LineNotifyService {
	Optional<LineNotify> findByCreatedBy(Integer createdBy);
    LineNotify save(LineNotify lineNotify);
    LineTokenResponse getAccessToken(LineTokenRequest request);
    LineStatusResponse getStatus(String token);
    void sendNotify(String message) throws Exception;
}
