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

    public String computeHmac(File file, String oldCrc, String newCrc) {
        byte[] hmacSha1 = null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "HmacSHA1");
            mac.init(secretKeySpec);
            String content = br.readLine();
            hmacSha1 = mac.doFinal(getReplace(oldCrc, newCrc, content).getBytes());
        } catch (NoSuchAlgorithmException
                | InvalidKeyException
                | IOException e) {
            log.error("Error crypting file {}", file.getName(), e);

        }
        return hex(hmacSha1);
    }

    private String getReplace(String oldCrc, String newCrc, String content) {
        if (oldCrc != null) {
            return content.replace(oldCrc, newCrc);
        } else {
            return content;
        }
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
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file));) {
            int cnt;
            while ((cnt = inputStream.read()) != -1) {
                crc.update(cnt);
            }
        } catch (IOException e) {
            log.error("Error computing crc32 for file {}", file.getName(), e);
        }
        return String.valueOf(crc.getValue());
    }

}
