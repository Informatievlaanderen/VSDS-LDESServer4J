package be.vlaanderen.informatievlaanderen.ldes.server.domain.encodig;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CharsetEncodingConfig {
	@Bean
	public FilterRegistrationBean<CharsetFilter> registerCharsetFilter() {
		FilterRegistrationBean<CharsetFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(new CharsetFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		return filterRegistrationBean;
	}
}
