package be.vlaanderen.informatievlaanderen.ldes.server.portconfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FetchEndpointFilter extends UrlExcludingFilter {
    private final String fetchPort;
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchEndpointFilter.class);

    public FetchEndpointFilter(String fetchPort) {
        super();
        this.fetchPort = fetchPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        int requestPort = request.getLocalPort();
        if (requestPort != Integer.parseInt(fetchPort) && request.getMethod().equalsIgnoreCase("GET")) {
            LOGGER.warn("Denying request from {} intended for fetch API on port {} received on port {}", request.getRemoteHost(), fetchPort, requestPort);
            response.setStatus(404);
            response.getOutputStream().close();
            return;
        }
        filterChain.doFilter(request, response);
    }
}
