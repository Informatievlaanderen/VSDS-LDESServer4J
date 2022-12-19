package be.vlaanderen.informatievlaanderen.ldes.server.rest.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rest")
public class RestConfig {

	public static final String CACHE_CONTROL_PUBLIC = "public";
	public static final String CACHE_CONTROL_IMMUTABLE = "immutable";
	public static final String CACHE_CONTROL_MAX_AGE = "max-age";

	public static final String TEXT_TURTLE = "text/turtle";
	public static final String INLINE = "inline";

	private int maxAge = 60;
	private int maxAgeImmutable = 604800;

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public int getMaxAgeImmutable() {
		return maxAgeImmutable;
	}

	public void setMaxAgeImmutable(int maxAgeImmutable) {
		this.maxAgeImmutable = maxAgeImmutable;
	}

	public String generateMutableCacheControl() {
		return List.of(CACHE_CONTROL_PUBLIC, CACHE_CONTROL_MAX_AGE + "=" + maxAge).stream()
				.collect(Collectors.joining(","));
	}

	public String generateImmutableCacheControl() {
		return List.of(CACHE_CONTROL_PUBLIC, CACHE_CONTROL_MAX_AGE + "=" + maxAgeImmutable, CACHE_CONTROL_IMMUTABLE)
				.stream()
				.collect(Collectors.joining(","));
	}
}
