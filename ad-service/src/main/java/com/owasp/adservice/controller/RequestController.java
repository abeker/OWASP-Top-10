package com.owasp.adservice.controller;

import com.owasp.adservice.dto.request.AdRequestRequest;
import com.owasp.adservice.dto.response.AdRequestResponse;
import com.owasp.adservice.dto.response.AdResponse;
import com.owasp.adservice.services.impl.RequestService;
import com.owasp.adservice.util.enums.RequestStatus;
import com.owasp.adservice.util.exceptions.GeneralException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService _requestService;

    public RequestController(RequestService requestService) {
        _requestService = requestService;
    }

    @PostMapping
    public void createRequest(@RequestBody List<AdRequestRequest> requestList) throws GeneralException {
        _requestService.proccessRequest(requestList);
    }

    @GetMapping("/{requestStatus}/agent/{id}")
    public List<AdRequestResponse> getAgentRequestsByStatus(@PathVariable("requestStatus") String requestStatus, @PathVariable("id") UUID agentId) throws GeneralException {
        return _requestService.getAgentRequestsByStatus(requestStatus, agentId);
    }

    @GetMapping("/{requestStatus}/simple-user/{id}")
    public List<AdRequestResponse> getSimpleUserRequestsByStatus(@PathVariable("requestStatus") String requestStatus, @PathVariable("id") UUID userId) throws GeneralException {
        return _requestService.getSimplUserRequestsByStatus(requestStatus, userId);
    }

}
