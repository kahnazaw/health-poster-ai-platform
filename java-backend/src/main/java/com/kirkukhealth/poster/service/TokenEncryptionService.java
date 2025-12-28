package com.kirkukhealth.poster.service;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Token Encryption Service
 * خدمة تشفير الرموز
 * 
 * Encrypts and decrypts OAuth tokens for secure storage
 * يشفر ويفك تشفير رموز OAuth للتخزين الآمن
 */
@Service
public class TokenEncryptionService {

    @Value("${jasypt.encryptor.password:KirkukHealth2024SecretKey}")
    private String encryptionPassword;

    private StringEncryptor encryptor;

    @PostConstruct
    public void init() {
        PooledPBEStringEncryptor pooledEncryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(encryptionPassword);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        pooledEncryptor.setConfig(config);
        this.encryptor = pooledEncryptor;
    }

    /**
     * Encrypt token
     * تشفير الرمز
     */
    public String encrypt(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return encryptor.encrypt(token);
    }

    /**
     * Decrypt token
     * فك تشفير الرمز
     */
    public String decrypt(String encryptedToken) {
        if (encryptedToken == null || encryptedToken.isEmpty()) {
            return null;
        }
        try {
            return encryptor.decrypt(encryptedToken);
        } catch (Exception e) {
            System.err.println("Error decrypting token: " + e.getMessage());
            return null;
        }
    }
}

