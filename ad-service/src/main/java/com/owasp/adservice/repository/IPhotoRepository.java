package com.owasp.adservice.repository;

import com.owasp.adservice.entity.Ad;
import com.owasp.adservice.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IPhotoRepository extends JpaRepository<Photo, UUID> {

    List<Photo> findAllByAd(Ad ad);

}
