package com.owasp.adservice.repository;

import com.owasp.adservice.entity.Ad;
import com.owasp.adservice.entity.Request;
import com.owasp.adservice.util.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRequestRepository extends JpaRepository<Request, UUID> {

    Request findOneById(UUID id);

    List<Request> findAllByStatus(RequestStatus requestStatus);

    List<Request> findAllByCustomerID(UUID id);

    List<Request> findAllByDeleted(boolean deleted);

    List<Request> findAllByCustomerIDAndStatus(UUID id, RequestStatus status);

    List<Request> findAllByAd(Ad ad);

}
