package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.ShaclCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.MemberIngestService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesShaclValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Tag(name = "Ingest")
public class LdesMemberIngestionController {
	private final MemberIngestService memberIngestService;
	private final ShaclCollection shaclCollection;

	public LdesMemberIngestionController(MemberIngestService memberIngestService,
			ShaclCollection shaclCollection) {
		this.memberIngestService = memberIngestService;
		this.shaclCollection = shaclCollection;
	}

	@PostMapping(value = "{collectionname}")
	@Operation(summary = "Ingest version object to collection")
	public void ingestLdesMember(
			@Parameter(schema = @Schema(implementation = String.class), description = "A valid RDF model of an LDES member") @RequestBody Member member,
			@PathVariable("collectionname") String collectionName) {
		validateMember(member, collectionName);
		memberIngestService.addMember(member);
	}

	private void validateMember(Member member, String collectionName) {
		Optional<ShaclShape> shape = shaclCollection.retrieveShape(collectionName);
		shape.ifPresent(shaclShape -> new LdesShaclValidator(shaclShape.getModel()).validate(member));
	}
}
