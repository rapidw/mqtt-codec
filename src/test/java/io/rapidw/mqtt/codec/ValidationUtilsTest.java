package io.rapidw.mqtt.codec;

import org.junit.jupiter.api.Test;
import static io.rapidw.mqtt.codec.ValidationUtils.*;
import static org.assertj.core.api.Assertions.*;

public class ValidationUtilsTest {

    @Test
    public void testMqttTopicFilterValidation() {
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("#/"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("#/"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("#/#"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("/#/"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("#s"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("#s/"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("#s/s"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("s/s#"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("/s/s#"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("s/#/"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("/s/#/"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("s/#/#"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("/s/#/#"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("s/#/s"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("/s/#/s"));
        assertThatCode(() -> validateAndEncodeTopicFilter("#")).doesNotThrowAnyException();
        assertThatCode(() -> validateAndEncodeTopicFilter("/#")).doesNotThrowAnyException();

        assertThatCode(() -> validateAndEncodeTopicFilter("/")).doesNotThrowAnyException();
        assertThatCode(() -> validateAndEncodeTopicFilter("//")).doesNotThrowAnyException();
        assertThatCode(() -> validateAndEncodeTopicFilter("//#")).doesNotThrowAnyException();

        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("/s+"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("s+/"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("/+s"));

        assertThatCode(() -> validateAndEncodeTopicFilter("+")).doesNotThrowAnyException();
        assertThatCode(() -> validateAndEncodeTopicFilter("+/")).doesNotThrowAnyException();
        assertThatCode(() -> validateAndEncodeTopicFilter("/+/")).doesNotThrowAnyException();

        assertThatCode(() -> validateAndEncodeTopicFilter("+/#")).doesNotThrowAnyException();
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("/+/#/"));
        assertThatIllegalArgumentException().isThrownBy(() -> validateAndEncodeTopicFilter("#/+"));
    }
}
