package csr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CrcExtractor {

    private Logger log = LoggerFactory.getLogger(CrcExtractor.class);

    public String readFromJson(File numberedFile) {
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
}
