package be.vlaanderen.informatievlaanderen.ldes.server.rest.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RestConfigTest {

    private RestConfig restConfig;

    @BeforeEach
    void setUp() {
        restConfig = new RestConfig();
    }

    @Test
    void generateMutableCacheControl_ShouldUseDefaultMaxAge_whenNextUpdateTsIsNull() {
        String cacheControl = restConfig.generateMutableCacheControl(null);

        assertThat(cacheControl).isEqualTo("public,max-age=60");
    }

    @Test
    void generateMutableCacheControl_ShouldUseDefaultMaxAge_whenNextUpdateTsIsInThePast() {
        String cacheControl = restConfig.generateMutableCacheControl(LocalDateTime.MIN);

        assertThat(cacheControl).isEqualTo("public,max-age=60");
    }

    @Test
    void generateMutableCacheControl_ShouldUseCalculatedMaxAge_whenNextUpdateTsIsInTheFuture() {
        String cacheControl = restConfig.generateMutableCacheControl(LocalDateTime.MAX);

        long substring = Long.parseLong(cacheControl.substring(cacheControl.indexOf("max-age=") + 8));
        assertThat(substring).isGreaterThan(9999999999L);
    }

}