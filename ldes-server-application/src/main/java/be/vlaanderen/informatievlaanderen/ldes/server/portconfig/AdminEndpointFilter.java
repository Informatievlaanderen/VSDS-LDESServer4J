package be.vlaanderen.informatievlaanderen.ldes.server.portconfig;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AdminEndpointFilter extends OncePerRequestFilter {
    private final String adminPort;
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminEndpointFilter.class);
    public AdminEndpointFilter(String adminPort) {
        this.adminPort = adminPort;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getLocalPort() != Integer.parseInt(adminPort)) {
            LOGGER.warn("Denying request from {} intended for admin API on port {} received on port {}", request.getRemoteHost(), adminPort, request.getLocalPort());
            response.setStatus(404);
            response.getOutputStream().close();
            return;
        }
        filterChain.doFilter(request, response);
    }
}
