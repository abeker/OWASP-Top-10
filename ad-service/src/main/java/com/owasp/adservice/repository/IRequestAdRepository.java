package com.owasp.adservice.repository;

import com.owasp.adservice.entity.Request;
import com.owasp.adservice.entity.RequestAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRequestAdRepository extends JpaRepository<RequestAd, UUID> {

    RequestAd findOneById(UUID id);

    List<RequestAd> findAllByAd(UUID adID);

    List<RequestAd> findAllByRequest(Request request);
}
