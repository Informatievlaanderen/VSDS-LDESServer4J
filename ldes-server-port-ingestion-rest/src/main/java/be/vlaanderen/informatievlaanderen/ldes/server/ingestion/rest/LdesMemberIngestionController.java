package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.CollectionNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.MemberIngestService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import org.springframework.web.bind.annotation.*;

@RestController
public class LdesMemberIngestionController {

	private final MemberIngestService memberIngestService;
	private final AppConfig appConfig;

	public LdesMemberIngestionController(MemberIngestService memberIngestService, AppConfig appConfig) {
		this.memberIngestService = memberIngestService;
		this.appConfig = appConfig;
	}

	@PostMapping(value = "{collectionname}")
	public void ingestLdesMember(@RequestBody Member member,
			@PathVariable("collectionname") String collectionName) {
		validateMember(member, collectionName);
		memberIngestService.addMember(member);
	}

	private void validateMember(Member member, String collectionName) {
		new LdesShaclValidator(
				appConfig.getLdesConfig(collectionName)
						.orElseThrow(() -> new CollectionNotFoundException(collectionName)))
				.validate(member);
	}
}