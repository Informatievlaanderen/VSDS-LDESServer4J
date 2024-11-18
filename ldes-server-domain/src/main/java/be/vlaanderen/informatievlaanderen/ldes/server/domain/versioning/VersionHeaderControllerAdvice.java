package be.vlaanderen.informatievlaanderen.ldes.server.domain.versioning;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class VersionHeaderControllerAdvice {
	private static final String APP_VERSION_HEADER_KEY = "X-App-Version";
	private final String appVersion;

	public VersionHeaderControllerAdvice(BuildProperties buildProperties) {
		this.appVersion = buildProperties.getVersion();
	}

	@ModelAttribute
	public void addVersionHeader(HttpServletResponse response) {
		response.setHeader(APP_VERSION_HEADER_KEY, appVersion);
	}
}
