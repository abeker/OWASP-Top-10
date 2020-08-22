package com.owasp.adservice.controller;

import com.owasp.adservice.services.impl.CommentService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService _commentService;

    public CommentController(CommentService commentService) {
        _commentService = commentService;
    }
}
