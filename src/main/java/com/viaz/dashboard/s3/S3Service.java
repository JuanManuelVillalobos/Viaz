package com.viaz.dashboard.s3;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.File;
import java.io.IOException;

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

    public byte[] getObject(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> res = s3.getObject(getObjectRequest);

        try {
            return res.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUploadUrl(String key) { // Add a parameter for the key
        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10)) // Set the expiration time (e.g., 10 minutes)
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            return presignedRequest.url().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating pre-signed URL", e);
        }
    }

    // Remember to close the S3Presigner when your application shuts down
    public void close() {
        s3Presigner.close();
    }
}