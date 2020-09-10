package csr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;

public class Extract {

    private Logger log = LoggerFactory.getLogger(Extract.class);

    private static final String JSON_FOLDER = "Edited";
    private static final String ORIGINAL_FOLDER = "Original";

    public void unzipAll() {
        String path = "./";
        File origDirectory = new File(path + ORIGINAL_FOLDER);
        File jsonDirectory = new File(path + JSON_FOLDER);

        File[] listFiles = origDirectory.listFiles();

        for (File file : listFiles) {
            String fileName = unzip(jsonDirectory, file);
            removeHexCode(fileName);
            deleteUnecessaryFile(fileName);
        }
    }

    private void deleteUnecessaryFile(String fileName) {
        try {
            Files.delete(new File(fileName).toPath());
        } catch (IOException e) {
            log.error("Error deleting file {}", fileName, e);
        }
    }

    private void removeHexCode(String fileName) {
        File json = new File(fileName);

        try (BufferedReader br = new BufferedReader(new FileReader(json));
             FileOutputStream fos = new FileOutputStream(json + ".json")) {

            // Skip first line hmac code
            String hexCode = br.readLine();
            if (hexCode == null) {
                log.error("{} is empty", fileName);
            }

            String st;
            while ((st = br.readLine()) != null) {
                fos.write(st.getBytes());
            }
        } catch (IOException e) {
            log.error("Error deleting hex code from file " + fileName, e);
        }
    }

    private String unzip(File jsonDirectory, File file) {
        String fileName = jsonDirectory.getAbsolutePath() + "/" + file.getName();

        try (FileInputStream fis = new FileInputStream(file.getAbsolutePath());
             GZIPInputStream gis = new GZIPInputStream(fis);
             FileOutputStream fos = new FileOutputStream(fileName);) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            //close resources
        } catch (IOException e) {
            log.error("Error decompressed hex code from file " + fileName, e);
        }
        return fileName;
    }
}
