package com.owasp.adservice.repository;

import com.owasp.adservice.entity.Comment;
import com.owasp.adservice.util.enums.CommentStatus;
import com.owasp.adservice.util.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ICommentRepository extends JpaRepository<Comment, UUID> {

    Comment findOneById(UUID id);

    List<Comment> findAllByStatus(CommentStatus status);
}
