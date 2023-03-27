package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest
class ShareItTests {

	@Test
	@Profile("Test")
	void contextLoads() {
	}

}
