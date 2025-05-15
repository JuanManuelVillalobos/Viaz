package com.viaz.dashboard.s3;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3;
    private final String bucketName;
    private final S3Presigner s3Presigner; // Add S3Presigner

    public S3Service(S3Client s3, @Value("${aws.bucket.main}") String bucketName) {
        this.s3 = s3;
        this.bucketName = bucketName;
        this.s3Presigner = S3Presigner.builder() // Initialize S3Presigner
                .region(s3.serviceClientConfiguration().region()) // Use the same region as your S3Client
                .credentialsProvider(s3.serviceClientConfiguration().credentialsProvider()) // Use the same credentials
                .build();
    }

    public PutObjectResponse putObject(String key, byte[] file) {
        System.out.println("Uploading to S3 bucket: " + bucketName + " with key: " + key + " ...");
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        PutObjectResponse response = s3.putObject(objectRequest, RequestBody.fromBytes(file));
        System.out.println("Done uploading to S3: " + key);
        return response;
    }


    public List<String> listObjects(String prefix) { // Add prefix parameter
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix) // Set the prefix
                .build();

        ListObjectsV2Response result = s3.listObjectsV2(listObjectsV2Request);

        return result.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }


    public String getDownloadUrl(String key) { // Add a parameter for the key
        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10)) // Set the expiration time (e.g., 10 minutes)
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating pre-signed URL", e);
        }
    }

    @PreDestroy
    public void close() {
        s3Presigner.close();
    }
}