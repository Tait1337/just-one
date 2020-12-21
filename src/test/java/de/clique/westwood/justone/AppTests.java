package de.clique.westwood.justone;

import de.clique.westwood.justone.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AppTests {

	@Autowired
	private GameService service;

	@Test
	void contextLoads() {
		assertThat(service).isNotNull();
	}

}
