package com.owasp.adservice.services;

import com.owasp.adservice.dto.request.AdRequestRequest;
import com.owasp.adservice.dto.response.AdRequestResponse;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IRequestService {

    void createRequest(AdRequestRequest requestList);

    void proccessRequest(List<AdRequestRequest> requestList);

    List<AdRequestResponse> getAgentRequestsByStatus(String requestStatus, UUID agentId);

    List<AdRequestResponse> getSimplUserRequestsByStatus(String requestStatus, UUID userId);

    Collection<AdRequestResponse> payRequest(UUID requestID, String token);

    Collection<AdRequestResponse> dropRequest(UUID requestID, String token);

    Collection<AdRequestResponse> approveRequest(UUID requestID, String token);

    Collection<AdRequestResponse> denyRequest(UUID requestID, String token);
}
