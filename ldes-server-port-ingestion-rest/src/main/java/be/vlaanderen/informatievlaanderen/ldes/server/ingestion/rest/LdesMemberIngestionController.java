package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services.MemberIngestService;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LdesMemberIngestionController {

    private final MeterRegistry registry;
	private final MemberIngestService memberIngestService;

	public LdesMemberIngestionController(final MeterRegistry meterRegistry,
                                         final MemberIngestService memberIngestService) {
        this.registry = meterRegistry;
        this.memberIngestService = memberIngestService;
	}

	@PostMapping(value = "${ldes.collectionname}", consumes = { "application/n-quads", "application/n-triples" })
	public void ingestLdesMember(@RequestBody LdesMember ldesMember) {
        registry.counter("saved_ldes_members").increment();
        memberIngestService.addMember(ldesMember);
	}
}