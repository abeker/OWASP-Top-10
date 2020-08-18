package com.owasp.adservice.services.impl;

import com.owasp.adservice.client.AuthClient;
import com.owasp.adservice.dto.response.AdResponse;
import com.owasp.adservice.dto.response.CarModelResponse;
import com.owasp.adservice.dto.response.CarResponse;
import com.owasp.adservice.dto.response.PhotoResponse;
import com.owasp.adservice.entity.Ad;
import com.owasp.adservice.entity.Photo;
import com.owasp.adservice.repository.IAdRepository;
import com.owasp.adservice.services.IAdService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdService implements IAdService {

    public final IAdRepository _adRepository;
    private final AuthClient _authClient;

    public AdService(IAdRepository adRepository, AuthClient authClient) {
        _adRepository = adRepository;
        _authClient = authClient;
    }

    @Override
    public List<AdResponse> getAds(boolean deleted, UUID agentID) {
        List<Ad> activeAds = getAdsByStatus(deleted);
        List<Ad> agentAds = getAgentAds(activeAds, agentID);
        return mapAdsToAdsResponse(agentAds);
    }

    private List<AdResponse> mapAdsToAdsResponse(List<Ad> agentAds) {
        List<AdResponse> adResponseList = new ArrayList<>();
        for (Ad ad : agentAds) {
            adResponseList.add(mapAdToAdResponse(ad));
        }
        return adResponseList;
    }

    private AdResponse mapAdToAdResponse(Ad ad) {
        AdResponse adResponse = new AdResponse();
        adResponse.setId(ad.getId());
        adResponse.setAgent(_authClient.getAgent(ad.getAgent()));
        adResponse.setAvailableKilometersPerRent(ad.getAvailableKilometersPerRent());
        adResponse.setCreationDate(ad.getCreationDate());
        adResponse.setLimitedDistance(ad.isLimitedDistance());
        adResponse.setSeats(ad.getSeats());
        createPhotoResponse(ad, adResponse);
        createCarResponse(ad, adResponse);
        return adResponse;
    }

    private void createCarResponse(Ad ad, AdResponse adResponse) {
        CarResponse carResponse = new CarResponse();
        CarModelResponse carModelResponse = new CarModelResponse();
        carModelResponse.setId(ad.getCar().getCarModel().getId());
        carModelResponse.setModelName(ad.getCar().getCarModel().getName());
        carModelResponse.setBrandName(ad.getCar().getCarModel().getCarBrand().getName());
        carModelResponse.setBrandCountry(ad.getCar().getCarModel().getCarBrand().getCountry());
        carModelResponse.setClassName(ad.getCar().getCarModel().getCarClass());
        carResponse.setCarModel(carModelResponse);
        carResponse.setId(ad.getCar().getId());
        carResponse.setFuelType(ad.getCar().getFuelType().toString());
        carResponse.setGearshiftType(ad.getCar().getGearshiftType().toString());
        carResponse.setKilometersTravelled(ad.getCar().getKilometersTraveled());
        carResponse.setNumberOfGears(ad.getCar().getNumberOfGears().toString());
        adResponse.setCar(carResponse);
    }

    private void createPhotoResponse(Ad ad, AdResponse adResponse) {
        List<PhotoResponse> photosOfAd = new ArrayList<>();
        for (Photo photo : ad.getAdPhotos()) {
            PhotoResponse photoResponse = new PhotoResponse();
            photoResponse.setId(photo.getId());
            photoResponse.setName(photo.getName());
            photoResponse.setPicByte(photo.getPicByte());
            photoResponse.setType(photo.getType());
            photosOfAd.add(photoResponse);
        }
        adResponse.setPhotos(photosOfAd);
    }

    private List<Ad> getAgentAds(List<Ad> activeAds, UUID agentID) {
        if(agentID == null) {
            return activeAds;
        }
        return activeAds.stream()
                    .filter(ad -> ad.getAgent().equals(agentID))
                    .collect(Collectors.toList());
    }

    private List<Ad> getAdsByStatus(boolean deleted) {
        return _adRepository.findAll()
                .stream()
                .filter(ad -> ad.isDeleted() == deleted)
                .collect(Collectors.toList());
    }

}
