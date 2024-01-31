package be.vlaanderen.informatievlaanderen.ldes.server.portconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${springdoc.swagger-ui.path:/swagger}")
    public String swaggerPath;
    @Value("${springdoc.api-docs.path:/v3/api-docs}")
    public String swaggerApiDocsPath;
    public static final String ADMIN_URL_PREFIX = "/admin/api/v1/*";
    public static final String ACTUATOR_URL_PREFIX = "/actuator";

    @Bean
    public FilterRegistrationBean<AdminEndpointFilter> registerAdminEndpointsFilter(@Value("${ldes-server.admin.port:${server.port:8080}}") String adminPort) {
        FilterRegistrationBean<AdminEndpointFilter> filterRegistrationBean = new FilterRegistrationBean<>(new AdminEndpointFilter(adminPort));
        filterRegistrationBean.addUrlPatterns(ADMIN_URL_PREFIX);
        getSwaggerPaths().forEach(filterRegistrationBean::addUrlPatterns);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<FetchEndpointFilter> registerFetchEndpointsFilter(@Value("${ldes-server.fetch.port:${server.port:8080}}") String fetchPort) {
        FetchEndpointFilter filter = new FetchEndpointFilter(fetchPort);
        filter.addExcludedUrl(ADMIN_URL_PREFIX);
        filter.addExcludedUrl(ACTUATOR_URL_PREFIX);
        filter.addExcludedUrls(getSwaggerPaths());
        FilterRegistrationBean<FetchEndpointFilter> filterRegistrationBean = new FilterRegistrationBean<>(filter);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<IngestEndpointFilter> registerIngestEndpointsFilter(@Value("${ldes-server.ingest.port:${server.port:8080}}") String ingestPort) {
        IngestEndpointFilter filter = new IngestEndpointFilter(ingestPort);
        filter.addExcludedUrl(ADMIN_URL_PREFIX);
        filter.addExcludedUrl(ACTUATOR_URL_PREFIX);
        filter.addExcludedUrls(getSwaggerPaths());
        FilterRegistrationBean<IngestEndpointFilter> filterRegistrationBean = new FilterRegistrationBean<>(filter);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    private List<String> getSwaggerPaths() {
        List<String> paths = new ArrayList<>();
        paths.add(swaggerPath + "/*");
        paths.add(swaggerPath.substring(0, swaggerPath.lastIndexOf("/")) + "/swagger-ui/*");
        paths.add(swaggerApiDocsPath);
        return paths;
    }
}
