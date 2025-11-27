package com.microtech.smartshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SmartshopApplicationTests {

	@Test
	void contextLoads() {
        assertEquals(2, 1 + 1);
	}

}
