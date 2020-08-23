package com.owasp.adservice.services;

import com.owasp.adservice.dto.request.AddAdRequest;
import com.owasp.adservice.dto.response.AdResponse;
import com.owasp.adservice.entity.Ad;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IAdService {

    List<AdResponse> getAds(boolean deleted, UUID agentID);

    AdResponse mapAdToAdResponse(Ad ad);

    AdResponse createAd(List<MultipartFile> fileList, AddAdRequest request);

    List<AdResponse> getAgentAds(boolean deleted, String token);

    String getAverageRateOfAd(Ad ad);
}
