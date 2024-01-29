package be.vlaanderen.informatievlaanderen.ldes.server.portconfig;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class EmbeddedTomcatConfiguration {

    @Bean
    public ConfigurableServletWebServerFactory servletContainer(@Value("${server.port:8080}") String serverPort,
                                                                @Value("${ldes-server.admin.port:${server.port:8080}}") String adminPort,
                                                                @Value("${ldes-server.ingest.port:${server.port:8080}}") String ingestPort,
                                                                @Value("${ldes-server.fetch.port:${server.port:8080}}") String fetchPort) {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

        List<String> occupiedPorts = new ArrayList<>();
        occupiedPorts.add(serverPort);
        List<String> additionalPorts = List.of(adminPort, ingestPort, fetchPort);

        for (String port: additionalPorts) {
            if (!occupiedPorts.contains(port)) {
                tomcat.addAdditionalTomcatConnectors(createConnector(port));
                occupiedPorts.add(port);
            }
        }

        return tomcat;
    }

    private Connector createConnector(String port) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(Integer.parseInt(port));
        return connector;
    }

}
