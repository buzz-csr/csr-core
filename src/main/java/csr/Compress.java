package csr;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class Compress {

    private Logger log = LoggerFactory.getLogger(Compress.class);

    private static final String JSON_FOLDER = "Edited";
    private static final String FINAL_FOLDER = "Final";

    public void zipAll() {
        String path = "./";

        zip(path, "nsb.json", "nsb", null, null);
        zip(path, "scb.json", "scb", null, null);

        File numberedFile = getNumberedFile(path);
        if (numberedFile != null && numberedFile.exists()) {
            String oldCrc = getOldCrc(numberedFile);
            String crc32 = new Checksum().computeCrc32(new File(path + JSON_FOLDER + "/nsb.json"));
            zip(path, numberedFile.getName(),
                    FilenameUtils.removeExtension(numberedFile.getName()), oldCrc, crc32);
        }

    }

    private File getNumberedFile(String path) {
        File jsonDirectory = new File(path + JSON_FOLDER);

        File file = Arrays.stream(jsonDirectory.listFiles())
                .filter(x -> !"nsb.json".equals(x.getName()) && !"scb.json".equals(x.getName()) && !"trb.json".equals(x.getName()))
                .findFirst()
                .orElse(null);

        return file;
    }

    private String getOldCrc(File numberedFile) {
        String oldCrc = null;
        try (InputStream fis = new FileInputStream(numberedFile); JsonReader reader = Json.createReader(fis);) {
            JsonObject root = reader.readObject();
            JsonArray profiles = root.getJsonArray("profileSaveHashes");
            for (int i = 0; i < profiles.size(); i++) {
                JsonObject profile = profiles.getJsonObject(i);
                String id = profile.getString("playerID");
                if (id != null && !id.equals("temp")) {
                    oldCrc = profile.getString("CRC");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return oldCrc;
    }

    public void zip(String path, String fileName, String targetName, String oldCrc, String newCrc) {
        File jsonDirectory = new File(path + JSON_FOLDER);

        File file = Arrays.stream(jsonDirectory.listFiles())
                .filter(x -> x.getName().equals(fileName))
                .findFirst()
                .orElse(null);

        String tempFile = createFilWithChecksum(path, fileName, targetName, file, oldCrc, newCrc);
        zipTempFile(path, fileName, targetName, tempFile);
        deleteTempFile(tempFile);
    }

    private void deleteTempFile(String tempFile) {
        try {
            Files.delete(new File(tempFile).toPath());
        } catch (IOException e) {
            log.error("Error deleting file " + tempFile, e);
        }
    }

    private void zipTempFile(String path, String fileName, String targetName, String tempFile) {
        try (FileInputStream fis = new FileInputStream(tempFile);
             FileOutputStream fos = new FileOutputStream(path + FINAL_FOLDER + "/" + targetName);
             GZIPOutputStream gzipOS = new GZIPOutputStream(fos);) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOS.write(buffer, 0, len);
            }
        } catch (IOException e) {
            log.error("Error compressing file " + fileName, e);
        }
    }

    public String createFilWithChecksum(String path, String fileName, String targetName, File file, String oldCrc, String newCrc) {
        Checksum checksum = new Checksum();
        String hmacSha1 = checksum.computeHmac(file, oldCrc, newCrc);

        String name = path + FINAL_FOLDER + "/" + targetName + ".temp";
        try (BufferedReader br = new BufferedReader(new FileReader(file));
             FileOutputStream fos = new FileOutputStream(name)) {
            fos.write(hmacSha1.getBytes());
            fos.write("\n".getBytes());


            String st;
            while ((st = br.readLine()) != null) {
                if (oldCrc != null) {
                    fos.write(st.replace(oldCrc, newCrc).getBytes());
                } else {
                    fos.write(st.getBytes());
                }
            }
        } catch (IOException e) {
            log.error("Error deleting hex code from file " + fileName, e);
        }
        return name;
    }
}