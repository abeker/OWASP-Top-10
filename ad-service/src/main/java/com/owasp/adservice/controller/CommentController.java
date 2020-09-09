package com.owasp.adservice.controller;

import com.owasp.adservice.dto.response.CommentResponse;
import com.owasp.adservice.services.ICommentService;
import com.owasp.adservice.util.exceptions.GeneralException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final ICommentService _commentService;

    public CommentController(ICommentService commentService) {
        _commentService = commentService;
    }

    @PostMapping("/ad/{adId}")
    @PreAuthorize("hasAuthority('POST_COMMENT')")
    public void postComment(@RequestHeader("Auth-Token") String token,
                            @PathVariable("adId") String adId,
                            @RequestBody String commentText) throws GeneralException {
        _commentService.postComment(token, UUID.fromString(adId), commentText);
    }

    @GetMapping("/{adId}")
    @PreAuthorize("hasAuthority('VIEW_COMMENT')")
    public List<CommentResponse> getCommentsByAdId(@PathVariable("adId") String adId) throws GeneralException {
        return _commentService.getComents(UUID.fromString(adId));
    }
}
