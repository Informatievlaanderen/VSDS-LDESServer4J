package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.MemberIngestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LdesMemberIngestionController {

	private final MemberIngestService memberIngestService;

	private static final Logger LOGGER = LoggerFactory.getLogger(LdesMemberIngestionController.class);

	public LdesMemberIngestionController(final MemberIngestService memberIngestService) {

		this.memberIngestService = memberIngestService;
	}

	@PostMapping(value = "${ldes.collectionname}")
	public void ingestLdesMember(@RequestBody Member member) {

		memberIngestService.addMember(member);
		LOGGER.debug("Ingested member with id " + member.getLdesMemberId());

		LOGGER.warn("Duplicate member ingested. Member with id " + member.getLdesMemberId() + " already exist");
	}
}