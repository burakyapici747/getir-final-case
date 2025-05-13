package com.burakyapici.library.config;

import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.*;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;

@Configuration
public class JwtConfig {

    @Bean
    public KeyPair keyPair() {
        try {
            byte[] privateKeyBytes = readKeyFromClasspath("keys/private.key");
            byte[] publicKeyBytes = readKeyFromClasspath("keys/public.key");

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            return new KeyPair(publicKey, privateKey);
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException("Failed to load RSA key pair from resources", e);
        }
    }

    private byte[] readKeyFromClasspath(String path) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("Key file not found: " + path);
            }
            return is.readAllBytes();
        }
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        JWK jwk = new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID("library-jwt-key")
            .build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
