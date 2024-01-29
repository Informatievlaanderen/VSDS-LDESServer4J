package be.vlaanderen.informatievlaanderen.ldes.server.portconfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

class AdminEndpointFilterTest {
    private final String ADMIN_PORT = "8081";
    private final String OTHER_PORT = "8082";
    private AdminEndpointFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private ServletOutputStream stream;

    @BeforeEach
    void setup() throws IOException {
        filter = new AdminEndpointFilter(ADMIN_PORT);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        stream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(stream);
    }

    @Test
    void when_RequestFromCorrectPort_Then_ContinueFilterChain() throws ServletException, IOException {
        when(request.getLocalPort()).thenReturn(Integer.parseInt(ADMIN_PORT));

        filter.doFilter(request, response, chain);

        verify(response, never()).setStatus(anyInt());
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void when_RequestFromDifferentPort_Then_StatusIs404() throws ServletException, IOException {
        when(request.getLocalPort()).thenReturn(Integer.parseInt(OTHER_PORT));

        filter.doFilter(request, response, chain);

        verify(response, times(1)).setStatus(404);
        verify(chain, never()).doFilter(request, response);
    }
}