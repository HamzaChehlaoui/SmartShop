package com.microtech.smartshop;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test - Disabled because we only use Unit Tests with Mockito.
 * This test requires a database connection (H2 or MySQL).
 */
@Disabled("Integration test not required - using Unit Tests only")
@SpringBootTest
class SmartshopApplicationTests {

	@Test
	void contextLoads() {
		// This would test if Spring context loads successfully
	}

}
