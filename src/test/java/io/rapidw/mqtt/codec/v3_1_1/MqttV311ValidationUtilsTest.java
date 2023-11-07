/**
 * Copyright 2023 Rapidw
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rapidw.mqtt.codec.v3_1_1;

import org.junit.jupiter.api.Test;

import static io.rapidw.mqtt.codec.utils.MqttV311ValidationUtils.validateAndEncodeTopicFilter;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class MqttV311ValidationUtilsTest {

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
