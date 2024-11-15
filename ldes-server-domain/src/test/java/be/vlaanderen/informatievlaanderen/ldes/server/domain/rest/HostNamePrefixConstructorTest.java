package be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class HostNamePrefixConstructorTest {

    private final String hostname = "http://localhost";
    private HostNamePrefixConstructor prefixConstructor;

    @Test
    void when_NotUsingRelativeUrls_Then_GetHostname() {
        prefixConstructor = new HostNamePrefixConstructor(hostname);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        request.setRequestURI("any/any");

        String prefix = prefixConstructor.buildPrefix();

        assertThat(prefix).isEqualTo(hostname);
    }
    @ParameterizedTest(name = "Request with URI {0} returns {1}")
    @ArgumentsSource(RequestUriArgumentsProvider.class)
    void when_UsingRelativeUrls_Then_GetCorrectPrefix(String requestUri, String expected) {
        prefixConstructor = new HostNamePrefixConstructor(hostname);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        request.setRequestURI(requestUri);

        String prefix = prefixConstructor.buildPrefix();

        assertThat(prefix).isEqualTo(expected);
    }

    static class RequestUriArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of("test", ".."),
                    Arguments.of("test/testing", "../.."),
                    Arguments.of("/test/testing", "../.."),
                    Arguments.of("", ""),
                    Arguments.of("test/testing/testing/testing", "../../../.."));
        }
    }
}