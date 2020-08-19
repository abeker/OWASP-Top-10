package com.owasp.adservice.services.impl;

import com.owasp.adservice.dto.request.AdRequestRequest;
import com.owasp.adservice.dto.response.AdRequestResponse;
import com.owasp.adservice.dto.response.AdResponse;
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

    public RequestService(IRequestRepository requestRepository, IAdRepository adRepository, IAdService adService) {
        _requestRepository = requestRepository;
        _adRepository = adRepository;
        _adService = adService;
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
        createdRequestResponse.setRequestStatus(request.getStatus().toString());
        createdRequestResponse.setPickUpAddress(request.getPickUpAddress());
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
