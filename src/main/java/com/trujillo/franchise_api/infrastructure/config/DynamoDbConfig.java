package com.trujillo.franchise_api.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient(
            @Value("${aws.region}") String region,
            @Value("${aws.dynamodb.endpoint:}") String endpoint) {

        DynamoDbAsyncClientBuilder builder = DynamoDbAsyncClient.builder()
                .region(Region.of(region));

        // Si hay endpoint, estamos en local: usamos DynamoDB Local con credenciales falsas
        if (!endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("local", "local")));
        }

        return builder.build();
    }
}
