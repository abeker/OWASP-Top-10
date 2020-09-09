package com.owasp.adservice.services.impl;

import com.owasp.adservice.client.AuthClient;
import com.owasp.adservice.dto.response.SimpleUserResponse;
import com.owasp.adservice.entity.Ad;
import com.owasp.adservice.entity.Rating;
import com.owasp.adservice.repository.IAdRepository;
import com.owasp.adservice.repository.IRatingRepository;
import com.owasp.adservice.services.IRatingService;
import com.owasp.adservice.util.exceptions.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@SuppressWarnings("unused")
@Service
public class RatingService implements IRatingService {

    private final Logger logger = LoggerFactory.getLogger(RatingService.class);

    private final IRatingRepository _ratingRepository;
    private final IAdRepository _adRepository;
    private final AuthClient _authClient;

    public RatingService(IRatingRepository ratingRepository, IAdRepository adRepository, AuthClient authClient) {
        _ratingRepository = ratingRepository;
        _adRepository = adRepository;
        _authClient = authClient;
    }

    @Override
    public void addRate(UUID adId, String token, String grade) throws GeneralException {
        SimpleUserResponse simpleUserResponse = _authClient.getSimpleUserFromToken(token);
        Ad ad = _adRepository.findOneById(adId);
        Rating rating = _ratingRepository.findOneBySimpleUserAndAd_Id(simpleUserResponse.getId(), adId);
        if(rating == null) {
            logger.info("[{}] set rate", simpleUserResponse.getUsername());
            Rating newRating = new Rating();
            newRating.setAd(ad);
            newRating.setGrade(grade);
            newRating.setSimpleUser(simpleUserResponse.getId());
            _ratingRepository.save(newRating);
        } else {
            logger.info("[{}] edit rate", simpleUserResponse.getUsername());
            rating.setGrade(grade);
            _ratingRepository.save(rating);
        }
    }


}
