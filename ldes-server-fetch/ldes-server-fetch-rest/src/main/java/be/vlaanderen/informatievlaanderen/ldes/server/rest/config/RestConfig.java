package be.vlaanderen.informatievlaanderen.ldes.server.rest.config;

import org.jetbrains.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;

@Configuration
@ConfigurationProperties(prefix = "rest")
public class RestConfig {

	public static final String CACHE_CONTROL_PUBLIC = "public";
	public static final String CACHE_CONTROL_IMMUTABLE = "immutable";
	public static final String CACHE_CONTROL_MAX_AGE = "max-age";

	public static final String INLINE = "inline";

	public static final int DEFAULT_MAX_AGE = 60;
	private int maxAge = DEFAULT_MAX_AGE;
	private int maxAgeImmutable = 31536000;

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public void setMaxAgeImmutable(int maxAgeImmutable) {
		this.maxAgeImmutable = maxAgeImmutable;
	}

	public String generateMutableCacheControl(@Nullable LocalDateTime nextUpdateTs) {
		final long secondsUntilNextUpdate =
				nextUpdateTs != null ? Duration.between(LocalDateTime.now(), nextUpdateTs).getSeconds() : -1;

        if (secondsUntilNextUpdate < 0) {
			return String.join(",", CACHE_CONTROL_PUBLIC, CACHE_CONTROL_MAX_AGE + "=" + maxAge);
		}

		return String.join(",", CACHE_CONTROL_PUBLIC, CACHE_CONTROL_MAX_AGE + "=" + secondsUntilNextUpdate);
	}

	public String generateImmutableCacheControl() {
		return String.join(",", CACHE_CONTROL_PUBLIC, CACHE_CONTROL_MAX_AGE + "=" + maxAgeImmutable,
				CACHE_CONTROL_IMMUTABLE);
	}
}
