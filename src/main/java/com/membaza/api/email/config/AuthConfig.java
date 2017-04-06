package com.membaza.api.email.config;

import com.membaza.api.email.persistence.ApiKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static java.util.Objects.requireNonNull;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@Configuration
public class AuthConfig extends GlobalAuthenticationConfigurerAdapter {

    private final MongoTemplate mongo;

    AuthConfig(MongoTemplate mongo) {
        this.mongo = requireNonNull(mongo);
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        final DaoAuthenticationProvider authProvider =
            new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(getUserDetailsService());
        authProvider.setPasswordEncoder(getPasswordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService getUserDetailsService() {
        return username -> mongo.findOne(
            query(where("name").is(username)),
            ApiKey.class
        );
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void init(
        AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(getUserDetailsService())
            .passwordEncoder(getPasswordEncoder());
    }
}