package be.vlaanderen.informatievlaanderen.ldes.server.portconfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class IngestEndpointFilterTest {
    private final String INGEST_PORT = "8081";
    private final String OTHER_PORT = "8082";
    private IngestEndpointFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private ServletOutputStream stream;

    @BeforeEach
    void setup() throws IOException {
        filter = new IngestEndpointFilter(INGEST_PORT);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        stream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(stream);
    }

    @Test
    void when_RequestFromCorrectPort_Then_ContinueFilterChain() throws ServletException, IOException {
        when(request.getLocalPort()).thenReturn(Integer.parseInt(INGEST_PORT));
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/");

        filter.doFilter(request, response, chain);

        verify(response, never()).setStatus(anyInt());
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void when_RequestFromDifferentPort_Then_StatusIs404() throws ServletException, IOException {
        when(request.getLocalPort()).thenReturn(Integer.parseInt(OTHER_PORT));
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/");

        filter.doFilter(request, response, chain);

        verify(response, times(1)).setStatus(404);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void when_RequestIsNotPOST_Then_ContinueFilterChain() throws ServletException, IOException {
        when(request.getLocalPort()).thenReturn(Integer.parseInt(OTHER_PORT));
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/");

        filter.doFilter(request, response, chain);

        verify(response, never()).setStatus(anyInt());
        verify(chain, times(1)).doFilter(request, response);
    }
    @Test
    void when_RequestFromDifferentPortAndShouldNotFilter_Then_ContinueFilterChain() throws ServletException, IOException {
        when(request.getLocalPort()).thenReturn(Integer.parseInt(OTHER_PORT));
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/excluded");
        filter.addExcludedUrl("/excluded");

        filter.doFilter(request, response, chain);

        verify(response, never()).setStatus(anyInt());
        verify(chain, times(1)).doFilter(request, response);
    }
}