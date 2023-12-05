package be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.USE_RELATIVE_URL_KEY;

@Component
public class PrefixConstructor {

    private final String hostname;
    private final boolean useRelativeUrl;
    public PrefixConstructor(@Value(HOST_NAME_KEY) String hostname, @Value(USE_RELATIVE_URL_KEY) boolean useRelativeUrl) {
        this.hostname = hostname;
        this.useRelativeUrl = useRelativeUrl;
    }

    public String buildPrefix() {
        if (!useRelativeUrl) {
            return hostname;
        }
        StringBuilder prefix = new StringBuilder();
        Arrays.stream(extractRequestURL().split("/")).filter(s->!s.isEmpty()).forEach(s-> prefix.append("."));
        return prefix.toString();
    }
    private String extractRequestURL() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return "";
        }
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        return request.getRequestURI();
    }
}
