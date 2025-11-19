package com.razz.fundingms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.razz.fundingms.repository")
public class MongoConfig {
    // Explicitly enables Mongo repositories; helps when auto-config might be disabled by property.
}

