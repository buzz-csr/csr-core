package csr;

import junit.framework.TestCase;
import org.junit.Test;

public class ExtractTest extends TestCase {

    private Extract extract = new Extract();

    @Test
    public void testUnzip() throws Exception {
        extract.unzipAll();
    }
}