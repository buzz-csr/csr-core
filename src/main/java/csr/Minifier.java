package csr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Minifier {

    private Logger log = LoggerFactory.getLogger(Minifier.class);

    public String minifyContent(File file) {
        String content = null;
        try (InputStream stream = new FileInputStream(file); JsonReader reader = Json.createReader(stream);) {
            JsonObject jsonObject = reader.readObject();
            content = jsonObject.toString();
        } catch (IOException e) {
            log.error("Error trying to minify {}", file.getName(), e);
        }
        return content;
    }
}
