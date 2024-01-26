package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnEnabledHealthIndicator("dcat")
public class DcatHealthIndicator implements HealthIndicator {

	private final DcatServerService dcatServerService;

	public DcatHealthIndicator(DcatServerService dcatServerService) {
		this.dcatServerService = dcatServerService;
	}

	@Override
	public Health health() {
		try {
			dcatServerService.getComposedDcat();
			return Health.up().build();
		} catch (ShaclValidationException exception) {
			return Health.status("INVALID").withException(exception).build();
		}
	}

}
