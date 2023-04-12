package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.MemberIngestService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesShaclValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
public class LdesMemberIngestionController {

	@Autowired
	private MemberIngestService memberIngestService;
	@Autowired
	private LdesShaclValidator validator;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@PostMapping(value = "{collectionname}")
	public void ingestLdesMember(@RequestBody @Validated Member member,
								 @PathVariable("collectionname") String collectionName) {
		memberIngestService.addMember(member);
	}
}