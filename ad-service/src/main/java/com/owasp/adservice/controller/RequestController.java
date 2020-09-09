package com.owasp.adservice.controller;

import com.owasp.adservice.dto.request.AdRequestRequest;
import com.owasp.adservice.dto.request.UnsafeUserRequest;
import com.owasp.adservice.dto.response.AdRequestResponse;
import com.owasp.adservice.services.IRequestService;
import com.owasp.adservice.services.impl.RequestService;
import com.owasp.adservice.util.exceptions.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/requests")
public class RequestController {

    private final IRequestService _requestService;

    public RequestController(RequestService requestService) {
        _requestService = requestService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_REQUEST')")
    public void createRequest(@RequestHeader("Auth-Token") String token,
                              @RequestBody List<AdRequestRequest> requestList) throws GeneralException {
        _requestService.proccessRequest(requestList, token);
    }

    @GetMapping("/{requestStatus}/agent/{id}")
    @PreAuthorize("hasAuthority('VIEW_AGENT_REQUESTS')")
    public List<AdRequestResponse> getAgentRequestsByStatus(@PathVariable("requestStatus") String requestStatus, @PathVariable("id") UUID agentId) throws GeneralException {
        return _requestService.getAgentRequestsByStatus(requestStatus, agentId);
    }

    @PutMapping("/{requestStatus}/simple-user/{id}")
    @PreAuthorize("hasAuthority('VIEW_USER_REQUESTS')")
    public List<AdRequestResponse> getSimpleUserRequestsByStatus(@RequestBody UnsafeUserRequest userRequest) throws GeneralException {
        return _requestService.getSimpleUserRequestsByStatus(userRequest.getRequestStatus(), UUID.fromString(userRequest.getCustomer_id()));
    }

    @PutMapping("/{requestId}/pay")
    @PreAuthorize("hasAuthority('PAY_REQUEST')")
    public ResponseEntity<Collection<AdRequestResponse>> userPay(@RequestHeader("Auth-Token") String token, @RequestBody String requestID){
        return new ResponseEntity<>(_requestService.payRequest(UUID.fromString(requestID), token), HttpStatus.OK);
    }

    @PutMapping("/{requestId}/drop")
    @PreAuthorize("hasAuthority('DROP_REQUEST')")
    public ResponseEntity<Collection<AdRequestResponse>> userDrop(@RequestHeader("Auth-Token") String token, @RequestBody String requestID){
        return new ResponseEntity<>(_requestService.dropRequest(UUID.fromString(requestID), token), HttpStatus.OK);
    }

    @PutMapping("/{requestID}/approve")
    @PreAuthorize("hasAuthority('APPROVE_REQUEST')")
    public ResponseEntity<Collection<AdRequestResponse>> approveRequest(@RequestHeader("Auth-Token") String token, @RequestBody String requestID){
        return new ResponseEntity<>(_requestService.approveRequest(UUID.fromString(requestID), token), HttpStatus.OK);
    }

    @PutMapping("/{requestID}/deny")
    @PreAuthorize("hasAuthority('DENY_REQUEST')")
    public ResponseEntity<Collection<AdRequestResponse>> denyRequest(@RequestHeader("Auth-Token") String token, @RequestBody String requestID){
        return new ResponseEntity<>(_requestService.denyRequest(UUID.fromString(requestID), token), HttpStatus.OK);
    }

}
