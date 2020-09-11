package csr;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

public class MinifierTest {

    @Test
    public void testMinifyContent() {
        String actual = new Minifier().minifyContent(new File("src/test/resources/jsonNotMinify.json"));
        Assertions.assertThat(actual).isEqualTo("{\"key1\":\"value2\",\"key2\":\"value2\"}");
    }
}