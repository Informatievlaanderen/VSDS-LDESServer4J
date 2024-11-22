package be.vlaanderen.informatievlaanderen.ldes.server.instrumentation;

import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "pyroscope.agent.enabled", havingValue = "true")
public class PyroscopeInitialiser {

	@PostConstruct
	public void init() {
		PyroscopeAgent.start(Config.build());
	}
}
