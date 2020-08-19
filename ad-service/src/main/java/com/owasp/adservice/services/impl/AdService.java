package com.owasp.adservice.services.impl;

import com.owasp.adservice.client.AuthClient;
import com.owasp.adservice.dto.AddAdRequest;
import com.owasp.adservice.dto.response.AdResponse;
import com.owasp.adservice.dto.response.CarModelResponse;
import com.owasp.adservice.dto.response.CarResponse;
import com.owasp.adservice.dto.response.PhotoResponse;
import com.owasp.adservice.entity.Ad;
import com.owasp.adservice.entity.Car;
import com.owasp.adservice.entity.CarModel;
import com.owasp.adservice.entity.Photo;
import com.owasp.adservice.repository.IAdRepository;
import com.owasp.adservice.repository.ICarModelRepository;
import com.owasp.adservice.repository.ICarRepository;
import com.owasp.adservice.repository.IPhotoRepository;
import com.owasp.adservice.services.IAdService;
import com.owasp.adservice.util.enums.FuelType;
import com.owasp.adservice.util.enums.GearshiftType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.Deflater;

@Service
public class AdService implements IAdService {

    public final IAdRepository _adRepository;
    private final AuthClient _authClient;
    private final ICarModelRepository _carModelRepository;
    private final ICarRepository _carRepository;
    private final IPhotoRepository _photoRepository;

    public AdService(IAdRepository adRepository, AuthClient authClient, ICarModelRepository carModelRepository, ICarRepository carRepository, IPhotoRepository photoRepository) {
        _adRepository = adRepository;
        _authClient = authClient;
        _carModelRepository = carModelRepository;
        _carRepository = carRepository;
        _photoRepository = photoRepository;
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

    @Override
    public AdResponse mapAdToAdResponse(Ad ad) {
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

    @Override
    public AdResponse createAd(List<MultipartFile> fileList, AddAdRequest request) {
        Car car = saveCar(request);
        Car savedCar = _carRepository.save(car);

        Ad ad = saveAd(request, savedCar);
        Ad savedAd = _adRepository.save(ad);
        savePhotos(fileList, savedAd);

        return mapAdToAdResponse(savedAd);
    }

    private Ad saveAd(AddAdRequest request, Car savedCar) {
        Ad ad = new Ad();
        ad.setAgent(request.getAgentId());
        ad.setCar(savedCar);
        ad.setLimitedDistance(request.isLimitedDistance());
        ad.setAvailableKilometersPerRent(request.getAvailableKilometersPerRent());
        ad.setSeats(request.getSeats());


        return ad;
    }

    private Car saveCar(AddAdRequest request) {
        Car car = new Car();
        CarModel carModel = findCarModel(request.getCarModel());
        GearshiftType gearshiftType = findGearshiftType(request.getGearshiftType());
        FuelType fuelType = findFuelType(request.getFuelType());
        car.setCarModel(carModel);
        car.setGearshiftType(gearshiftType);
        car.setFuelType(fuelType);
        car.setKilometersTraveled(request.getKilometersTraveled());
        return car;
    }

    private void savePhotos(List<MultipartFile> fileList, Ad savedAd) {
        for (MultipartFile file : fileList) {
            Photo photo = new Photo();
            photo.setAd(savedAd);
            photo.setName(file.getOriginalFilename());
            photo.setType(file.getContentType());
            try {
                photo.setPicByte(compressBytes(file.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            _photoRepository.save(photo);
        }
    }

    private CarModel findCarModel(String carModelString) {
        String[] carModelArray = carModelString.split(",");
        String carBrand = carModelArray[0].trim();
        String carModelName = carModelArray[1].trim();
        for (CarModel carModel : _carModelRepository.findAll()) {
            if(carModel.getCarBrand().getName().equalsIgnoreCase(carBrand)
                    && carModel.getName().equalsIgnoreCase(carModelName)){
                return carModel;
            }
        }
        return null;
    }

    private GearshiftType findGearshiftType(String gearshiftTypeString) {
        if(gearshiftTypeString.trim().equalsIgnoreCase("MANUAL")) {
            return GearshiftType.MANUAL;
        } else if(gearshiftTypeString.trim().equalsIgnoreCase("AUTOMATIC")) {
            return GearshiftType.AUTOMATIC;
        } else {
            return GearshiftType.SEMIAUTOMATIC;
        }
    }

    private FuelType findFuelType(String fuelTypeString) {
        if(fuelTypeString.trim().equalsIgnoreCase("DIESEL")) {
            return FuelType.DIESEL;
        } else if (fuelTypeString.trim().equalsIgnoreCase("BENZINE")) {
            return FuelType.BENZINE;
        } else {
            return FuelType.GAS;
        }
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

    public static byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException ignored) {
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

}
