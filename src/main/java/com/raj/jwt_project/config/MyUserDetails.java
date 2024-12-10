//we can work for authorization and authentication by implementing the userdetails interface.
// it represents the user. provides email, password, authorities.
//it concretes of user implementation


package com.raj.jwt_project.config;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raj.jwt_project.Entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

//we can make it for authorization and authentication
@NoArgsConstructor
@Data
public class MyUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;

    @JsonIgnore
    private String password;


    public MyUserDetails(Long id,  String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public static MyUserDetails build(User user) {

        return new MyUserDetails(
                user.getId(), user.getEmail(), user.getPassword()
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MyUserDetails user = (MyUserDetails) o;
        return Objects.equals(id, user.id);
    }
}
