package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import org.junit.Test;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MongoAutoConfiguration.class)
public class SpringContextTest {
	
	public SpringContextTest() {
		
	}

    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}