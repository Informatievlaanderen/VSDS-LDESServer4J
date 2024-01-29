package be.vlaanderen.informatievlaanderen.ldes.server.portconfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class IngestEndpointFilter extends UrlExcludingFilter {
    private final String ingestPort;
    private static final Logger LOGGER = LoggerFactory.getLogger(IngestEndpointFilter.class);

    public IngestEndpointFilter(String ingestPort) {
        super();
        this.ingestPort = ingestPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getLocalPort() != Integer.parseInt(ingestPort) && request.getMethod().equalsIgnoreCase("POST")) {
            LOGGER.warn("Denying request from {} intended for ingest endpoint on port {} received on port {}", request.getRemoteHost(), ingestPort, request.getLocalPort());
            response.setStatus(404);
            response.getOutputStream().close();
            return;
        }
        filterChain.doFilter(request, response);
    }
}
