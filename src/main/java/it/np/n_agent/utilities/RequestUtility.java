package it.np.n_agent.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.np.n_agent.exception.WebhookMainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class RequestUtility {

    private final static Logger log = LoggerFactory.getLogger(RequestUtility.class);

    private final String webhookSecret;
    private final ObjectMapper objectMapper;

    protected RequestUtility(@Value("${webhook.github.secret}")String webhookSecret, ObjectMapper objectMapper) {
        this.webhookSecret = webhookSecret;
        this.objectMapper = objectMapper;
    }

    public <T> T parsePayload(String rawPayload, Class<T> clazz) {
        try {
            return objectMapper.readValue(rawPayload, clazz);
        } catch (Exception e) {
            log.error("Error parsing payload: {}", e.getMessage());
            throw new WebhookMainException("Invalid JSON payload", HttpStatus.BAD_REQUEST);
        }
    }

    public void validateHmac(String payload, String signature) {
        try {
            String algorithm = "HmacSHA256";
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8),
                    algorithm
            );
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            String expectedSignature = "sha256=" + bytesToHex(hash);

            if (MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8)
            )) {
                return;
            }

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error validating HMAC: {}", e.getMessage());
        }
        throw new WebhookMainException("Invalid HMAC signature", HttpStatus.UNAUTHORIZED);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
