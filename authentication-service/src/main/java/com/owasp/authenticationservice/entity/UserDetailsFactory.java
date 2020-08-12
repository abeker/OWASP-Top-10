package com.owasp.authenticationservice.entity;

public class UserDetailsFactory {

    private UserDetailsFactory() {
    }

    /**
     * Creates UserDetailsImpl from a user.
     *
     * @param user user model
     * @return UserDetailsImpl
     */
    public static UserDetailsImpl create(User user) {
        return new UserDetailsImpl(user.getId(), user.getUsername(), user.getPassword(), user.getAuthorities());
    }
}
