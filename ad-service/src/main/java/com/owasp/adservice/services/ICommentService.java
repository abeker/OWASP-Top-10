package com.owasp.adservice.services;

import java.util.UUID;

public interface ICommentService {

    void postComment(String token, UUID adId, String commentText);

}
