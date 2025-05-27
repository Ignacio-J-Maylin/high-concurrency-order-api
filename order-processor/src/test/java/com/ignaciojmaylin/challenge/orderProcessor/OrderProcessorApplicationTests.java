package com.ignaciojmaylin.challenge.orderProcessor;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Deshabilitado temporalmente mientras se ajustan los beans necesarios")
class OrderProcessorApplicationTests {

	@Test
	void contextLoads() {
	}
}
