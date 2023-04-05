package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services.MemberIngestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LdesMemberIngestionController {

	private final MemberIngestService memberIngestService;

	public LdesMemberIngestionController(final MemberIngestService memberIngestService) {

		this.memberIngestService = memberIngestService;
	}

	@PostMapping(value = "${ldes.collectionname}", consumes = { "application/n-quads", "application/n-triples" })
	public void ingestLdesMember(@RequestBody LdesMember ldesMember) {

		memberIngestService.addMember(ldesMember);
	}
}