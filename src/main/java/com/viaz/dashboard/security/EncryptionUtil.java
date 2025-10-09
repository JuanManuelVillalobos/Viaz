package com.viaz.dashboard.security;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12; // 96 bits
    private static final int TAG_LENGTH_BIT = 128;

    private final SecretKeySpec secretKey;

    public EncryptionUtil(@Value("${encryption.key}") String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        this.secretKey = new SecretKeySpec(decodedKey, "AES");
    }

    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes());

            // Combine IV and ciphertext
            byte[] encryptedWithIv = new byte[IV_LENGTH + encrypted.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, IV_LENGTH);
            System.arraycopy(encrypted, 0, encryptedWithIv, IV_LENGTH, encrypted.length);

            return Base64.getEncoder().encodeToString(encryptedWithIv);
        } catch (Exception e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    public String decrypt(String cipherText) {
        try {
            byte[] decoded = Base64.getDecoder().decode(cipherText);

            byte[] iv = new byte[IV_LENGTH];
            byte[] encrypted = new byte[decoded.length - IV_LENGTH];

            System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);
            System.arraycopy(decoded, IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Decryption error", e);
        }
    }
}
