package com.owasp.adservice.services.impl;

import com.owasp.adservice.client.AuthClient;
import com.owasp.adservice.dto.response.CommentResponse;
import com.owasp.adservice.dto.response.SimpleUserResponse;
import com.owasp.adservice.entity.Ad;
import com.owasp.adservice.entity.Comment;
import com.owasp.adservice.repository.IAdRepository;
import com.owasp.adservice.repository.ICommentRepository;
import com.owasp.adservice.services.IAdService;
import com.owasp.adservice.services.ICommentService;
import com.owasp.adservice.util.enums.CommentStatus;
import com.owasp.adservice.util.exceptions.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService implements ICommentService {

    private final ICommentRepository _commentRepository;
    private final AuthClient _authClient;
    private final IAdRepository _adRepository;
    private final IAdService _adService;

    public CommentService(ICommentRepository commentRepository, AuthClient authClient, IAdRepository adRepository, IAdService adService) {
        _commentRepository = commentRepository;
        _authClient = authClient;
        _adRepository = adRepository;
        _adService = adService;
    }

    @Override
    public void postComment(String token, UUID adId, String commentText) {
        SimpleUserResponse simpleUser = _authClient.getSimpleUserFromToken(token);
        Ad ad = _adRepository.findOneById(adId);
        if(simpleUser == null) {
            throw new GeneralException("User doesn't exist.", HttpStatus.BAD_REQUEST);
        } else if (isUserCommentAd(ad, simpleUser)) {
            throw new GeneralException("User already comment this ad.", HttpStatus.BAD_REQUEST);
        }
        saveComment(simpleUser, ad, commentText);
    }

    @Override
    public List<CommentResponse> getComents(UUID adId) {
        Ad ad = _adRepository.findOneById(adId);
        return _adService.mapCommentsToCommentResponse(ad.getComments());
    }

    private void saveComment(SimpleUserResponse simpleUser, Ad ad, String commentText) {
        Comment comment = new Comment();
        comment.setAd(ad);
        comment.setSimpleUser(simpleUser.getId());
        comment.setStatus(CommentStatus.PENDING);
        comment.setText(commentText);
        _commentRepository.save(comment);
    }

    private boolean isUserCommentAd(Ad ad, SimpleUserResponse simpleUser) {
        for (Comment comment : ad.getComments()) {
            if(comment.getSimpleUser().equals(simpleUser.getId())) {
                return true;
            }
        }
        return false;
    }
}
