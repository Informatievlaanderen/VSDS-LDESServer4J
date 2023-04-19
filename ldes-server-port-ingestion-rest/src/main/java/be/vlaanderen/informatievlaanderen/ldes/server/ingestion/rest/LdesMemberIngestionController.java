package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.MemberIngestService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LdesMemberIngestionController {
	private final MemberIngestService memberIngestService;
	private final AppConfig appConfig;
	private final Map<String, Model> shapes;

	public LdesMemberIngestionController(MemberIngestService memberIngestService, AppConfig appConfig) {
		this.memberIngestService = memberIngestService;
		this.appConfig = appConfig;
		this.shapes = new HashMap<>();
	}

	@PostMapping(value = "{collectionname}")
	public void ingestLdesMember(@RequestBody Member member,
			@PathVariable("collectionname") String collectionName) {
		validateMember(member, collectionName);
		memberIngestService.addMember(member);
	}

	private void validateMember(Member member, String collectionName) {
		Model shape = shapes.get(collectionName);
		new LdesShaclValidator(shape, appConfig.getLdesConfig(collectionName)).validate(member);
	}

	@EventListener
	public void handleShaclChangedEvent(ShaclChangedEvent event) {
		shapes.put(event.getCollectionName(), event.getShacl());
	}
}
