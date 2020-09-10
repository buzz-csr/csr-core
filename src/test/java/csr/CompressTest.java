package csr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class CompressTest {

    private Compress compress = new Compress();

    @Test
    public void testZipAll() throws Exception {
        compress.zipAll();
    }

}
