package csr;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;

public class ChecksumTest {

    private Checksum checksum = new Checksum();

    @Test
    public void testComputeHmac() throws Exception {
        String actual = checksum.computeHmac(new File("C:/Dev/temp/csr/Backup/nsb.test"), null, null);
        assertThat(actual).isEqualTo("2b5955bb727ebe813608fc9c5028a5660e0016e3");
    }

    @Test
    public void testComputeCrc32() throws Exception {
        assertThat(checksum.computeCrc32(new File("C:/Dev/temp/csr/Backup/nsb.test"))).isEqualTo("2408858343");
    }

}
