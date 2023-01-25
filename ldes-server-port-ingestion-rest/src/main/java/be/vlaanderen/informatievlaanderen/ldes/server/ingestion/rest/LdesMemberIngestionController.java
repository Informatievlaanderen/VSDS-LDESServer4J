package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.MemberIngestService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesShaclValidator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LdesMemberIngestionController {

	private final MemberIngestService memberIngestService;
	private final LdesShaclValidator ldesShaclValidator;

	public LdesMemberIngestionController(final MemberIngestService memberIngestService,
			LdesShaclValidator ldesShaclValidator) {

		this.memberIngestService = memberIngestService;
		this.ldesShaclValidator = ldesShaclValidator;
	}

	@PostMapping(value = "${ldes.collectionname}")
	public void ingestLdesMember(@RequestBody @Validated Member member) {
		memberIngestService.addMember(member);
	}
}