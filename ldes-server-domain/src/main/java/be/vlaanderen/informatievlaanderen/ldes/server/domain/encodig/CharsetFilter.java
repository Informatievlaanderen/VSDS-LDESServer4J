package be.vlaanderen.informatievlaanderen.ldes.server.domain.encodig;

import jakarta.servlet.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CharsetFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		chain.doFilter(request, response);
	}
}
