package com.membaza.api.email.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.membaza.api.email.persistence.Privilege.CREATE_API_KEYS_PRIVILEGE;
import static com.membaza.api.email.persistence.Privilege.CREATE_TEMPLATES_PRIVILEGE;
import static com.membaza.api.email.persistence.Privilege.READ_TEMPLATES_PRIVILEGE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers(POST, "/email/keys").hasAuthority(CREATE_API_KEYS_PRIVILEGE.name())
            .antMatchers(POST, "/email/templates").hasAuthority(CREATE_TEMPLATES_PRIVILEGE.name())
            .antMatchers(GET, "/email/templates/**").hasAuthority(READ_TEMPLATES_PRIVILEGE.name())
            .anyRequest().fullyAuthenticated()
            .and().httpBasic()
            .and().csrf().disable();
    }
}