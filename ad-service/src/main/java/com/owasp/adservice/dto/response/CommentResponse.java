package com.owasp.adservice.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class CommentResponse {

    private UUID id;

    private String text;

    private UUID simpleUserId;

    private String commentStatus;

}
