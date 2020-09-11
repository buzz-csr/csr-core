package csr;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Checksum {

    private Logger log = LoggerFactory.getLogger(Checksum.class);

    private static final String KEY = "4cPw3ZyC";

    public String computeHmac(String content) {
        byte[] hmacSha1 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "HmacSHA1");
            mac.init(secretKeySpec);
            hmacSha1 = mac.doFinal(content.getBytes());
        } catch (NoSuchAlgorithmException
                | InvalidKeyException e) {
            log.error("Error crypting file", e);

        }
        return hex(hmacSha1);
    }

    private String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x", aByte));
        }
        return result.toString();
    }

    public String computeCrc32(File file) {
        CRC32 crc = new CRC32();
        String content = new Minifier().minifyContent(file);
        crc.update(content.getBytes());
        return String.valueOf(crc.getValue());
    }
}
