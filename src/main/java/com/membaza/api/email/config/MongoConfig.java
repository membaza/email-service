package com.membaza.api.email.config;

import com.mongodb.Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@Configuration
public class MongoConfig {

    @Autowired
    private Environment env;

    @Bean
    public MongoTemplate mongoTemplate(Mongo mongo) throws Exception {
        return new MongoTemplate(
            mongo, env.getProperty("spring.data.mongodb.database")
        );
    }
}
