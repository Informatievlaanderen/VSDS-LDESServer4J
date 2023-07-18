package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.MemberIngester;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberIngestController implements OpenApiMemberIngestController {

	private final MemberIngester memberIngester;

	public MemberIngestController(MemberIngester memberIngester) {
		this.memberIngester = memberIngester;
	}

	@Override
	@PostMapping(value = "{collectionName}")
	public void ingestLdesMember(@RequestBody Member member, @PathVariable String collectionName) {
		memberIngester.ingest(member);
	}
}
