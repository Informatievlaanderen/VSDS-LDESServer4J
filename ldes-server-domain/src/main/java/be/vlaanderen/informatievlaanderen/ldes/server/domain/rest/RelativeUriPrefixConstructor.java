package be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Component
@ConditionalOnProperty(value = "ldes-server.use-relative-url", havingValue = "true")
public class RelativeUriPrefixConstructor implements UriPrefixConstructor {
	@Override
	public String buildPrefix() {
		StringBuilder prefix = new StringBuilder();
		Arrays.stream(extractRequestURL().split("/")).filter(s -> !s.isEmpty()).forEach(s -> prefix.append("/.."));
		if (!prefix.isEmpty()) {
			prefix.deleteCharAt(0);
		}
		return prefix.toString();
	}

	private String extractRequestURL() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			return "";
		}
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		return request.getRequestURI();
	}
}
