package com.notier;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NotiErApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void runTest() {
        Assertions.assertThat(1).isEqualTo(1);
    }

}
