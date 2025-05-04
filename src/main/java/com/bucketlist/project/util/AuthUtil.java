package com.bucketlist.project.util;

import com.bucketlist.project.exceptions.PermissionDeniedException;
import com.bucketlist.project.model.AppRole;
import com.bucketlist.project.model.User;
import com.bucketlist.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    UserRepository userRepository;

    public String loggedInEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

        return user.getEmail();
    }

    public Long loggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

        return user.getUserId();
    }

    public User loggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
        return user;
    }

    public void checkAdmin(String resource, String action) {
        User user = loggedInUser();
        if (user.getRole().getRoleName() != AppRole.ROLE_ADMIN) {
            throw new PermissionDeniedException("User", user.getUserId(), resource, action);
        }
    }

    public void checkOwnerOrAdmin(User currentUser, User resourceOwner, String resource, String action) {
        boolean isAdmin = currentUser.getRole().getRoleName() == AppRole.ROLE_ADMIN;
        boolean isOwner = resourceOwner.getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isOwner) {
            throw new PermissionDeniedException("User", currentUser.getUserId(), resource, action);
        }
    }

}