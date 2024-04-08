package be.vlaanderen.informatievlaanderen.ldes.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

import java.net.*;

@Configuration
public class ActuatorHostNameCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    private static final String HOST_NAME_KEY = "${ldes-server.host-name}";
    private final String hostName;

    public ActuatorHostNameCustomizer(@Value(HOST_NAME_KEY) String hostName) {
        this.hostName = hostName;
    }

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        try {
            final URL url = URI.create(hostName).toURL();
            final InetAddress address = InetAddress.getByName(url.getHost());
            factory.setAddress(address);
        } catch (UnknownHostException | MalformedURLException e) {
            throw new IllegalArgumentException("ldes-server.host-name is invalid", e);
        }
    }

}
