package com.owasp.adservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
public class CommentResponse {

    private UUID id;

    private String text;

    private SimpleUserResponse simpleUser;

    private String commentStatus;

    private Date postTime;

}
