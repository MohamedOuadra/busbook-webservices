package com.busbook.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.busbook.catalog.controller.HealthController;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CatalogServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private HealthController healthController;

	@Test
	void healthEndpointReturnsUp() {
		Map<String, String> response = healthController.health();

		assertEquals("catalog-service", response.get("service"));
		assertEquals("UP", response.get("status"));
	}


}
