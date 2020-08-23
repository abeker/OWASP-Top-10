package com.owasp.adservice.services.impl;

import com.owasp.adservice.client.AuthClient;
import com.owasp.adservice.dto.response.SimpleUserResponse;
import com.owasp.adservice.entity.Ad;
import com.owasp.adservice.entity.Comment;
import com.owasp.adservice.repository.IAdRepository;
import com.owasp.adservice.repository.ICommentRepository;
import com.owasp.adservice.services.ICommentService;
import com.owasp.adservice.util.enums.CommentStatus;
import com.owasp.adservice.util.exceptions.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentService implements ICommentService {

    private final ICommentRepository _commentRepository;
    private final AuthClient _authClient;
    private final IAdRepository _adRepository;

    public CommentService(ICommentRepository commentRepository, AuthClient authClient, IAdRepository adRepository) {
        _commentRepository = commentRepository;
        _authClient = authClient;
        _adRepository = adRepository;
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
