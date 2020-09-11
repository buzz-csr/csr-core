package csr;

import org.junit.Test;

public class CompressTest {

    private Compress compress = new Compress();

    @Test
    public void testZipAll() throws Exception {
        compress.zipAll("./");
    }

}
