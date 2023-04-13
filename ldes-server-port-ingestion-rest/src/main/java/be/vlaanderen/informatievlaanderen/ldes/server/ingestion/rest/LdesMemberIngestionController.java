package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.MemberIngestService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesShaclValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LdesMemberIngestionController {
	private final MemberIngestService memberIngestService;
	private final LdesConfigModelService configModelService;
	private final AppConfig appConfig;

	@Autowired
	public LdesMemberIngestionController(MemberIngestService memberIngestService,
			LdesConfigModelService configModelService, AppConfig appConfig) {
		this.memberIngestService = memberIngestService;
		this.configModelService = configModelService;
		this.appConfig = appConfig;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		LdesConfigModel shape = configModelService.retrieveShape(ldesConfig.getCollectionName());
		LdesShaclValidator validator = new LdesShaclValidator(shape.getModel(), ldesConfig);
		binder.setValidator(validator);
	}

	@PostMapping(value = "{collectionname}")
	public void ingestLdesMember(@RequestBody Member member,
			@PathVariable("collectionname") String collectionName) {
		validateMember(member, collectionName);
		memberIngestService.addMember(member);
	}

	private void validateMember(Member member, String collectionName) {
		new LdesShaclValidator(appConfig.getLdesConfig(collectionName)).validate(member);
	}
}