package com.owasp.adservice.services.impl;

import com.owasp.adservice.client.AuthClient;
import com.owasp.adservice.dto.request.AdRequestRequest;
import com.owasp.adservice.dto.response.AdRequestResponse;
import com.owasp.adservice.dto.response.AdResponse;
import com.owasp.adservice.dto.response.AgentResponse;
import com.owasp.adservice.dto.response.SimpleUserResponse;
import com.owasp.adservice.entity.Ad;
import com.owasp.adservice.entity.Request;
import com.owasp.adservice.repository.IAdRepository;
import com.owasp.adservice.repository.IRequestRepository;
import com.owasp.adservice.services.IAdService;
import com.owasp.adservice.services.IRequestService;
import com.owasp.adservice.util.enums.RequestStatus;
import com.owasp.adservice.util.exceptions.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequestService implements IRequestService {

    private final IRequestRepository _requestRepository;
    private final IAdRepository _adRepository;
    private final IAdService _adService;
    private final AuthClient _authClient;

    public RequestService(IRequestRepository requestRepository, IAdRepository adRepository, IAdService adService, AuthClient authClient) {
        _requestRepository = requestRepository;
        _adRepository = adRepository;
        _adService = adService;
        _authClient = authClient;
    }

    @Override
    public void proccessRequest(List<AdRequestRequest> requestList) throws GeneralException {
        for (AdRequestRequest request : requestList) {
            Ad ad = _adRepository.findOneById(request.getAdID());
            if(isCarAvailable(ad, request)) {
                createRequest(request);
            }
            else {
                throw new GeneralException("Car " + ad.getCar().getCarModel().getCarBrand().getName() + " " +
                     ad.getCar().getCarModel().getName() + " is not available in this period.", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Override
    public List<AdRequestResponse> getAgentRequestsByStatus(String requestStatusString, UUID agentId) {
        RequestStatus requestStatus = getRequestStatusFromString(requestStatusString);
        List<Request> activeRequests = getActiveRequests();
        List<Request> agentRequests = getAgentRequests(activeRequests, requestStatus, agentId);

        return mapRequestsToAdRequestResponse(agentRequests);
    }

    private List<AdRequestResponse> mapRequestsToAdRequestResponse(List<Request> agentRequests) {
        List<AdRequestResponse> adRequestResponseList = new ArrayList<>();
        for (Request request : agentRequests) {
            adRequestResponseList.add(mapSingleRequestToAdRequestResponse(request));
        }
        return adRequestResponseList;
    }

    private AdRequestResponse mapSingleRequestToAdRequestResponse(Request request) {
        AdRequestResponse createdRequestResponse = new AdRequestResponse();
        createdRequestResponse.setId(request.getId());
        createdRequestResponse.setRequestStatus(request.getStatus().toString());
        createdRequestResponse.setPickUpAddress(request.getPickUpAddress());
        createdRequestResponse.setPickUpDate(request.getPickUpDate().toString());
        createdRequestResponse.setPickUpTime(request.getPickUpTime().toString());
        createdRequestResponse.setReturnDate(request.getReturnDate().toString());
        createdRequestResponse.setReturnTime(request.getReturnTime().toString());
        createdRequestResponse.setAgent(_authClient.getAgent(request.getAd().getAgent()));
        if(request.getCustomerID() != null) {
            createdRequestResponse.setSimpleUser(_authClient.getSimpleUser(request.getCustomerID()));
        } else {
            createdRequestResponse.setSimpleUser(null);
        }
        AdResponse adResponse = _adService.mapAdToAdResponse(request.getAd());
        createdRequestResponse.setAd(adResponse);

        return createdRequestResponse;
    }

    private List<Request> getAgentRequests(List<Request> activeRequests, RequestStatus requestStatus, UUID agentId) {
        List<Request> agentRequests = new ArrayList<>();
        for (Request request : activeRequests) {
            if(request.getStatus().equals(requestStatus)) {
                if(request.getAd().getAgent().equals(agentId)) {
                    agentRequests.add(request);
                }
            }
        }
        return agentRequests;
    }

    private RequestStatus getRequestStatusFromString(String requestStatusString) {
        RequestStatus requestStatus;
        switch (requestStatusString) {
            case "PAID": requestStatus = RequestStatus.PAID;
            break;
            case "CANCELED": requestStatus = RequestStatus.CANCELED;
            break;
            case "RESERVED": requestStatus = RequestStatus.RESERVED;
            break;
            default: requestStatus = RequestStatus.PENDING;
        }

        return requestStatus;
    }

    private List<Request> getActiveRequests() {
        return _requestRepository.findAll()
                .stream()
                .filter(request -> !request.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<AdRequestResponse> getSimplUserRequestsByStatus(String requestStatusString, UUID userId) {
        RequestStatus requestStatus = getRequestStatusFromString(requestStatusString);
        List<Request> activeRequests = getActiveRequests();
        List<Request> simpleUserRequests = getSimpleUserRequests(activeRequests, requestStatus, userId);

        return mapRequestsToAdRequestResponse(simpleUserRequests);
    }

    @Override
    public Collection<AdRequestResponse> payRequest(UUID requestID, String token) {
        SimpleUserResponse simpleUserResponse = _authClient.getSimpleUserFromToken(token);
        Request request = _requestRepository.findOneById(requestID);
        if(request.getStatus().equals(RequestStatus.RESERVED)) {
            request.setStatus(RequestStatus.PAID);
            _requestRepository.save(request);
        }

        changeStatusOfRequests(request, RequestStatus.RESERVED, RequestStatus.CANCELED);
        return getSimplUserRequestsByStatus("RESERVED", simpleUserResponse.getId());
    }

    @Override
    public Collection<AdRequestResponse> dropRequest(UUID requestID, String token) {
        SimpleUserResponse simpleUserResponse = _authClient.getSimpleUserFromToken(token);
        Request request = _requestRepository.findOneById(requestID);
        RequestStatus retStatus = request.getStatus();
        if(!request.getStatus().equals(RequestStatus.PAID)) {
            request.setStatus(RequestStatus.CANCELED);
            _requestRepository.save(request);
        }

        return getSimplUserRequestsByStatus(retStatus.toString(), simpleUserResponse.getId());
    }

    @Override
    public Collection<AdRequestResponse> approveRequest(UUID requestID, String token) {
        AgentResponse agentResponse = _authClient.getAgentFromToken(token);
        Request request = _requestRepository.findOneById(requestID);
        request.setStatus(RequestStatus.RESERVED);
        _requestRepository.save(request);

        TimerTask taskPaid = new TimerTask() {
            public void run() {
                System.out.println("Approved request performed on: " + LocalTime.now() + ", " +
                        "Request id: " + Thread.currentThread().getName());
                if(!request.getStatus().equals(RequestStatus.PAID)) {
                    request.setStatus(RequestStatus.CANCELED);
                    _requestRepository.save(request);
                }
            }
        };
        Timer timer = new Timer(request.getId().toString());
        long delay = (12 * 60 * 60 * 1000);
        System.out.println("Approved request received at: " + LocalTime.now());
        timer.schedule(taskPaid, delay);

        return getAgentRequestsByStatus("PENDING", agentResponse.getId());
    }

    @Override
    public Collection<AdRequestResponse> denyRequest(UUID requestID, String token) {
        AgentResponse agentResponse = _authClient.getAgentFromToken(token);
        Request request = _requestRepository.findOneById(requestID);
        if(request.getStatus().equals(RequestStatus.PENDING)) {
            request.setStatus(RequestStatus.CANCELED);
            _requestRepository.save(request);
        }

        return getAgentRequestsByStatus("PENDING", agentResponse.getId());
    }

    public void changeStatusOfRequests(Request baseRequest, RequestStatus wakeUpStatus, RequestStatus finalStatus) {
        for (Request requestCheck : _requestRepository.findAll()) {
            if (requestCheck.getStatus().equals(wakeUpStatus)
                    && checkRequestMatching(baseRequest, requestCheck)) {
                requestCheck.setStatus(finalStatus);
                _requestRepository.save(requestCheck);
            }
        }
    }

    public boolean checkRequestMatching(Request requestFirst, Request requestSecond) {
        if(requestFirst.getAd().getId().equals(requestSecond.getAd().getId())) {
            if (requestFirst.getReturnDate().isBefore(requestSecond.getPickUpDate())) {
                return false;
            } else {
                if (requestFirst.getPickUpDate().isAfter(requestSecond.getReturnDate())) {
                    return false;
                } else {
                    if (requestFirst.getReturnDate().isEqual(requestSecond.getPickUpDate())) {
                        return requestFirst.getReturnTime().isAfter(requestSecond.getPickUpTime());
                    } else if (requestSecond.getReturnDate().isEqual(requestFirst.getPickUpDate())) {
                        return requestFirst.getPickUpTime().isBefore(requestSecond.getReturnTime());
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<Request> getSimpleUserRequests(List<Request> activeRequests, RequestStatus requestStatus, UUID userId) {
        List<Request> simpleUserRequests = new ArrayList<>();
        for (Request request : activeRequests) {
            if(request.getStatus().equals(requestStatus)) {
                if(request.getCustomerID().equals(userId)) {
                    simpleUserRequests.add(request);
                }
            }
        }
        return simpleUserRequests;
    }

    /**
     * Check whether the car is available in that period
     * */
    private boolean isCarAvailable(Ad ad, AdRequestRequest requestDTO) {
        List<Request> requestAdList = _requestRepository.findAllByAd(ad);
        for (Request request : requestAdList) {
            if(request.getStatus().equals(RequestStatus.PAID)) {
                return false;
            }
            boolean startEndDate = request.getReturnDate().isBefore(LocalDate.parse(requestDTO.getPickUpDate()));
            if (!startEndDate) {
                boolean endStartDate = LocalDate.parse(requestDTO.getReturnDate()).isBefore(request.getPickUpDate());
                if (!endStartDate) {
                    if(request.getReturnDate().isEqual(LocalDate.parse(requestDTO.getPickUpDate()))) {
                        if (!request.getReturnTime().isBefore(LocalTime.parse(requestDTO.getPickUpTime()))) {
                            return false;
                        }
                    }
                    else if(request.getPickUpDate().isEqual(LocalDate.parse(requestDTO.getReturnDate()))) {
                        if (!request.getPickUpTime().isAfter(LocalTime.parse(requestDTO.getReturnTime()))) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void createRequest(AdRequestRequest requestDTO) {
        Request request = new Request();
        UUID simpleUserId = null;
        if(requestDTO.getCustomerID() != null) {
            simpleUserId = requestDTO.getCustomerID();
        }

        request.setCustomerID(simpleUserId);
        createRequestDetails(request, requestDTO);
        _requestRepository.save(request);

        // TODO Implement Saga Pattern

        addTimer(24, request);
    }

    private void addTimer(int delayInHours, Request request) {
        TimerTask taskPending = new TimerTask() {
            public void run() {
                System.out.println("Request performed on: " + LocalTime.now() + ", " +
                        "Request id: " + Thread.currentThread().getName());
                if(request.getStatus().equals(RequestStatus.PENDING)) {
                    request.setStatus(RequestStatus.CANCELED);
                    _requestRepository.save(request);
                }
            }
        };
        Timer timer = new Timer(request.getId().toString());
        long delay = (delayInHours * 60 * 60 * 1000);
        System.out.println("Request received at: " + LocalTime.now());
        timer.schedule(taskPending, delay);
    }

    private void createRequestDetails(Request request, AdRequestRequest requestDTO) {
        request.setStatus(RequestStatus.PENDING);
        request.setPickUpAddress(requestDTO.getPickUpAddress());
        request.setPickUpDate(LocalDate.parse(requestDTO.getPickUpDate()));
        request.setPickUpTime(LocalTime.parse(requestDTO.getPickUpTime()));
        request.setReturnDate(LocalDate.parse(requestDTO.getReturnDate()));
        request.setReturnTime(LocalTime.parse(requestDTO.getReturnTime()));
        request.setAd(_adRepository.findOneById(requestDTO.getAdID()));
    }

}
