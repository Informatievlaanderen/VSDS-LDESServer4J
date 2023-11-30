package be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;

@Component
public class RequestContextExtracter {

    private final String hostname;
    public RequestContextExtracter(@Value(HOST_NAME_KEY) String hostname) {
        this.hostname = hostname;
    }
    public String extractRequestURL() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null : "Should only be called in context of a request";
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        return hostname + request.getRequestURI();
    }
}
