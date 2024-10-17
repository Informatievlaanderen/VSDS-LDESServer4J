package be.vlaanderen.informatievlaanderen.ldes.server.instrumentation;

import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PyroscopeInitialiser {

    @Value("${pyroscope.agent.enabled:false}")
    private boolean usePyroscope;
    @PostConstruct
    public void init() {
        if(usePyroscope) {
            PyroscopeAgent.start(
                    Config.build()
            );
        }
    }
}
