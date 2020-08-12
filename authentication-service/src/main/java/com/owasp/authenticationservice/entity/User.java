package com.owasp.authenticationservice.entity;

import com.owasp.authenticationservice.util.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@Entity(name = "user_entity")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements Serializable {

    @Column(unique = true, nullable = false)
    private String username; //email

    @Column(nullable = false)
    private String password;

    private String firstName;

    private String lastName;

    private boolean deleted;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private Timestamp lastPasswordResetDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private Set<Authority> authorities;

    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auth_list = new ArrayList<>();
        this.authorities.forEach(authority -> auth_list.addAll(authority.getPermissions()));
        return auth_list;
    }

    public Set<Authority> getRoles() {
        return this.authorities;
    }
}
