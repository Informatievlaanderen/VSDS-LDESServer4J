package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.MemberIngester;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Ingest")
public class MemberIngestController {

	private final MemberIngester memberIngester;

	public MemberIngestController(MemberIngester memberIngester) {
		this.memberIngester = memberIngester;
	}

	@PostMapping(value = "{collectionname}")
	@Operation(summary = "Ingest version object to collection")
	public void ingestLdesMember(@RequestBody Member member) {
		memberIngester.ingest(member);
	}

}
