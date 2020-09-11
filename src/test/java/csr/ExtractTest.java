package csr;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ExtractTest extends TestCase {

    private Extract extract = new Extract();

    @Before
    public void setup() throws IOException {
        File[] files = getFiles();
        if (files != null) {
            for (File f : files) {
                FileUtils.forceDelete(f);
            }
        }
    }

    private File[] getFiles() {
        File file = new File("src/test/resources/Edited");
        File[] files = null;
        if (file.exists()) {
            files = file.listFiles();
        }
        return files;
    }

    @Test
    public void testUnzip() throws Exception {
        extract.unzipAll("./src/test/resources/");
        File[] files = getFiles();
        Assertions.assertThat(files).isNotNull().hasSize(3);
    }
}