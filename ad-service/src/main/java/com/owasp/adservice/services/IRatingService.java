package com.owasp.adservice.services;

import com.owasp.adservice.util.exceptions.GeneralException;

import java.util.UUID;

public interface IRatingService {

    void addRate(UUID adId, String token, String grade) throws GeneralException;

}
