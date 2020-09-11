package csr;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;

public class ChecksumTest {

    private Checksum checksum = new Checksum();

    @Test
    public void testComputeHmac() throws Exception {
        String content = new Minifier().minifyContent(new File("src/test/resources/nsb.json"));
        String actual = checksum.computeHmac(content);
        assertThat(actual).isEqualTo("b5cfe3bdaea824b98d14772f148ac1466ef37620");
    }

    @Test
    public void testComputeCrc32() throws Exception {
        assertThat(checksum.computeCrc32(new File("src/test/resources/nsb.json"))).isEqualTo("4289034266");
    }

}
