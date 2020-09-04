package com.owasp.adservice.services;

import com.owasp.adservice.dto.response.CommentResponse;

import java.util.List;
import java.util.UUID;

public interface ICommentService {

    void postComment(String token, UUID adId, String commentText);

    List<CommentResponse> getComents(UUID adId);
}
