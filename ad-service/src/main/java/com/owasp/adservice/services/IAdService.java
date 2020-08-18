package com.owasp.adservice.services;

import com.owasp.adservice.dto.response.AdResponse;

import java.util.List;
import java.util.UUID;

public interface IAdService {

    List<AdResponse> getAds(boolean deleted, UUID agentID);

}
