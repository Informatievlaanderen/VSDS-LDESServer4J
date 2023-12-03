package be.vlaanderen.informatievlaanderen.ldes.server.domain.instrumentation;

import org.springframework.stereotype.Component;
import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import io.pyroscope.javaagent.EventType;
import io.pyroscope.http.Format;
import jakarta.annotation.PostConstruct;
@Component
public class PyroscopeInitialiser {


    @PostConstruct
    public void init() {

        PyroscopeAgent.start(
                new Config.Builder()
                        .setApplicationName("ldes-server")
                        .setProfilingEvent(EventType.ITIMER)
                        .setFormat(Format.JFR)
                        .setServerAddress("http://pyroscope-server:4040")
                        // Optionally, if authentication is enabled, specify the API key.
                        // .setAuthToken(System.getenv("PYROSCOPE_AUTH_TOKEN"))
                        // Optionally, if you'd like to set allocation threshold to register events, in bytes. '0' registers all events
                        // .setProfilingAlloc("0")
                        .build()
        );
    }
}
