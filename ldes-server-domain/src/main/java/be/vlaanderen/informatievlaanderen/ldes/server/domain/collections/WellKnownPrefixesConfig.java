package be.vlaanderen.informatievlaanderen.ldes.server.domain.collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class WellKnownPrefixesConfig {

	@Bean
	public Prefixes wellKnownPrefixes() {
		final Map<String, String> prefixes = readPrefixes();
		return () -> prefixes;
	}


	private Map<String, String> readPrefixes() {
		try {
			final File wellKnownPrefixes = ResourceUtils.getFile("classpath:well-known-prefixes.csv");
			return Files.readAllLines(wellKnownPrefixes.toPath()).stream()
					.map(line -> line.split(","))
					.collect(Collectors.toMap(line -> line[0], line -> line[1]));
		} catch (IOException e) {
			return Map.of();
		}
	}
}
