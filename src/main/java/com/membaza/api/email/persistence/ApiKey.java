package com.membaza.api.email.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@Data
@Document(collection = "apikeys")
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties("password")
public final class ApiKey implements UserDetails {

    private @Id String id;
    private @Indexed(unique = true) String username;
    private @NotNull String password;
    private Set<Privilege> authorities;

    public boolean has(Privilege privilege) {
        return authorities.contains(privilege);
    }

    @Override @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
            .map(Privilege::name)
            .map(SimpleGrantedAuthority::new)
            .collect(toSet());
    }

    @Override @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}