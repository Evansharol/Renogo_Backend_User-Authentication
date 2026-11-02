package com.example.renogo.security;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@Component
public class PasswordService {

    private static final String HASH_PREFIX = "pbkdf2-sha256";
    private static final int ITERATIONS = 310_000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BYTES = 32;
    private static final int MAX_PASSWORD_BYTES = 1024;
    private final SecureRandom secureRandom = new SecureRandom();

    public String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (rawPassword.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new IllegalArgumentException("Password must be at most 1024 UTF-8 bytes");
        }

        byte[] salt = new byte[SALT_BYTES];
        secureRandom.nextBytes(salt);
        byte[] hash = deriveKey(rawPassword, salt, ITERATIONS, HASH_BYTES);
        return HASH_PREFIX + "$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null
                || rawPassword.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            return false;
        }

        if (storedPassword.startsWith(HASH_PREFIX + "$")) {
            try {
                String[] parts = storedPassword.split("\\$", -1);
                if (parts.length != 4 || !HASH_PREFIX.equals(parts[0])) {
                    return false;
                }
                int iterations = Integer.parseInt(parts[1]);
                if (iterations < 100_000 || iterations > 1_000_000) {
                    return false;
                }
                byte[] salt = Base64.getDecoder().decode(parts[2]);
                byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
                if (salt.length != SALT_BYTES || expectedHash.length != HASH_BYTES) {
                    return false;
                }
                byte[] actualHash = deriveKey(rawPassword, salt, iterations, HASH_BYTES);
                return MessageDigest.isEqual(expectedHash, actualHash);
            } catch (IllegalArgumentException exception) {
                return false;
            }
        }
        return MessageDigest.isEqual(
                rawPassword.getBytes(StandardCharsets.UTF_8),
                storedPassword.getBytes(StandardCharsets.UTF_8));
    }

    public boolean isLegacyPassword(String storedPassword) {
        return storedPassword != null && !storedPassword.startsWith(HASH_PREFIX + "$");
    }

    private byte[] deriveKey(String password, byte[] salt, int iterations, int keyLengthBytes) {
        PBEKeySpec keySpec = new PBEKeySpec(
                password.toCharArray(), salt, iterations, keyLengthBytes * 8);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(keySpec)
                    .getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Password hashing is unavailable", exception);
        } finally {
            keySpec.clearPassword();
        }
    }
}
