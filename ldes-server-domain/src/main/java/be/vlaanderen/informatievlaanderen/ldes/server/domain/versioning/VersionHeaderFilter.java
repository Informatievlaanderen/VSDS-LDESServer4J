package be.vlaanderen.informatievlaanderen.ldes.server.domain.versioning;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class VersionHeaderFilter implements Filter {
	private static final String APP_VERSION_HEADER_KEY = "X-App-Version";
	private final String appVersion;

	public VersionHeaderFilter(String appVersion) {
		this.appVersion = appVersion;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (response instanceof HttpServletResponse httpResponse) {
			httpResponse.setHeader(APP_VERSION_HEADER_KEY, appVersion);
		}
		chain.doFilter(request, response);
	}
}
