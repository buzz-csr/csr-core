package csr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Compress {

    private Logger log = LoggerFactory.getLogger(Compress.class);

    private static final String JSON_FOLDER = "Edited";
    private static final String FINAL_FOLDER = "Final";

    public void zipAll(String path) {
        checkFinalDir(path);
        zip(path, "nsb.json", "nsb", null, null);
        zip(path, "scb.json", "scb", null, null);
        zip(path, "trb.json", "trb", null, null);

        File numberedFile = getNumberedFile(path);
        if (numberedFile != null && numberedFile.exists()) {
            String oldCrc = new CrcExtractor().readFromJson(numberedFile);
            String crc32 = new Checksum().computeCrc32(new File(path + JSON_FOLDER + "/nsb.json"));
            zip(path, numberedFile.getName(), FilenameUtils.removeExtension(numberedFile.getName()), oldCrc, crc32);
        }

    }

    private void checkFinalDir(String path) {
        File finalDir = new File(path + FINAL_FOLDER);
        if (!finalDir.exists()) {
            finalDir.mkdir();
        }
    }

    private File getNumberedFile(String path) {
        File jsonDirectory = new File(path + JSON_FOLDER);

        return Arrays.stream(jsonDirectory.listFiles())
                .filter(x -> !"nsb.json".equals(x.getName()) && !"scb.json".equals(x.getName())
                        && !"trb.json".equals(x.getName()))
                .findFirst()
                .orElse(null);
    }

    public void zip(String path, String fileName, String targetName, String oldCrc, String newCrc) {
        File jsonDirectory = new File(path + JSON_FOLDER);

        if (jsonDirectory.listFiles() != null) {
            File file = Arrays.stream(jsonDirectory.listFiles())
                    .filter(x -> x.getName().equals(fileName))
                    .findFirst()
                    .orElse(null);

            if (file != null) {
                String tempFile = createFilWithChecksum(path, fileName, targetName, file, oldCrc, newCrc);
                zipTempFile(path, fileName, targetName, tempFile);
                deleteTempFile(tempFile);
            }
        } else {
            log.info("Any file to compress");
        }

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

    private String createFilWithChecksum(String path, String fileName, String targetName, File file, String oldCrc,
            String newCrc) {
        Checksum checksum = new Checksum();

        String content = new Minifier().minifyContent(file);
        if (oldCrc != null) {
            content = content.replace(oldCrc, newCrc);
        }
        String hmacSha1 = checksum.computeHmac(content);

        String name = path + FINAL_FOLDER + "/" + targetName + ".temp";
        try (FileOutputStream fos = new FileOutputStream(name)) {
            fos.write(hmacSha1.getBytes());
            fos.write("\n".getBytes());
            fos.write(content.getBytes());
        } catch (IOException e) {
            log.error("Error deleting hex code from file " + fileName, e);
        }
        return name;
    }

}
