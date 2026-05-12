package com.qazaq.telecom;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires external PostgreSQL configuration that is not available in unit test runs")
class TelecomApplicationTests {

	@Test
	void contextLoads() {
	}

}
