package com.viaz.dashboard.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    private final String awsRegion;
    private final boolean mock;

    public S3Config(@Value("${aws.region}") String awsRegion, @Value("${aws.s3.mock}") boolean mock) {
        this.awsRegion = awsRegion;
        this.mock = mock;
    }

    @Bean
    public S3Client s3Client() {
        if (mock) {
            return new FakeS3();
        }
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(S3Client s3Client) {
        if (mock) {
            return null;
        }
        return S3Presigner.builder()
                .region(s3Client.serviceClientConfiguration().region())
                .credentialsProvider(s3Client.serviceClientConfiguration().credentialsProvider())
                .build();
    }

}
