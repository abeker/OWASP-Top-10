package com.owasp.authenticationservice.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CreateSimpleUserRequest {

    @NotNull(message = "Username is mandatory")
    @Size(min=8, max=30, message = "Username length must be between 8 and 30 characters.")
    @Pattern.List({
        @Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
                message = "Email must satisfy an email format."),
        @Pattern(regexp = "^(?!<.+?>).*$", message = "Username cannot contain html elements.")
    })
    private String username;

    @NotNull(message = "First name is mandatory")
    @Size(min=3, max=30, message = "First name length must be between 3 and 30 characters.")
    @Pattern(regexp = "^(?!<.+?>).*$", message = "First name cannot contain html elements.")
    private String firstName;

    @NotNull(message = "Last name is mandatory")
    @Size(min=3, max=30, message = "Last name length must be between 3 and 30 characters.")
    @Pattern(regexp = "^(?!<.+?>).*$", message = "Last name cannot contain html elements.")
    private String lastName;

    @NotNull(message = "Password is mandatory")
    @Pattern.List({
        @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&].{9,}",
                message = "Password must contain digit, special character, lowercase and uppercase letter. Min length is 9."),
        @Pattern(regexp = "^(?!<.+?>).*$", message = "Password cannot contain html elements.")
    })
    private String password;

    @NotNull(message = "Repeated password is mandatory")
    @Size(min = 9, message = "Password length must be greater than 9 characters.")
    @Pattern.List({
        @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&].{9,}",
                message = "Password must contain digit, special character, lowercase and uppercase letter."),
        @Pattern(regexp = "^(?!<.+?>).*$", message = "Repeated password cannot contain html elements.")
    })
    private String rePassword;

    @NotNull(message = "SSN is mandatory")
    @Size(min=13, max=13, message = "SSN length must be exactly 13 characters.")
    @Pattern.List({
        @Pattern(regexp = "^[0-9]*$", message = "SSN must contain only digits."),
        @Pattern(regexp = "^(?!<.+?>).*$", message = "SSN cannot contain html elements.")
    })
    private String ssn;

    @NotNull(message = "Address is mandatory")
    @Size(min=4, max=30, message = "Address length must be between 4 and 30 characters.")
    @Pattern(regexp = "^(?!<.+?>).*$", message = "Address cannot contain html elements.")
    private String address;
}
