package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Properties;

@Getter
@Setter
@RequiredArgsConstructor
public class EndpointConfig extends Properties {
    private final String endpoint;
}
