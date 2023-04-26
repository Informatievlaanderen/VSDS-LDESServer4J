package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.ShaclCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.MemberIngestService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class LdesMemberIngestionController {
	private final MemberIngestService memberIngestService;
	private final AppConfig appConfig;

	private final ShaclCollection shaclCollection;

	public LdesMemberIngestionController(MemberIngestService memberIngestService, AppConfig appConfig,
			ShaclCollection shaclCollection) {
		this.memberIngestService = memberIngestService;
		this.appConfig = appConfig;
		this.shaclCollection = shaclCollection;
	}

	@PostMapping(value = "{collectionname}")
	public void ingestLdesMember(@RequestBody Member member,
			@PathVariable("collectionname") String collectionName) {
		validateMember(member, collectionName);
		memberIngestService.addMember(member);
	}

	private void validateMember(Member member, String collectionName) {
		Optional<ShaclShape> shape = shaclCollection.retrieveShape(collectionName);
		shape.ifPresent(
				shaclShape -> new LdesShaclValidator(shaclShape.getModel(), appConfig.getLdesConfig(collectionName))
						.validate(member));
	}
}
