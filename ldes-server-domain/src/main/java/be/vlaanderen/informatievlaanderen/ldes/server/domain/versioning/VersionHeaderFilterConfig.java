package be.vlaanderen.informatievlaanderen.ldes.server.domain.versioning;

import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VersionHeaderFilterConfig {

	@Bean
	public FilterRegistrationBean<VersionHeaderFilter> registerVersionHeaderFilter(BuildProperties buildProperties) {
		FilterRegistrationBean<VersionHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(new VersionHeaderFilter(buildProperties.getVersion()));
		filterRegistrationBean.addUrlPatterns("/*");
		return filterRegistrationBean;
	}
}
