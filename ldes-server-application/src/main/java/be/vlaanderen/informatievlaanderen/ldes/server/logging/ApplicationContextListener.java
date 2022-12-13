package be.vlaanderen.informatievlaanderen.ldes.server.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
@Component
public class ApplicationContextListener implements ApplicationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextListener.class);
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            final Environment env = ((ContextRefreshedEvent) event).getApplicationContext()
                    .getEnvironment();
            LOGGER.info("Active profiles: " + Arrays.toString(env.getActiveProfiles()));
        }
    }
}
