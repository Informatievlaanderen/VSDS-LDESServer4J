package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.statistics.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.apache.jena.atlas.json.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.statistics.service.StatisticsConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

@Service
public class StatisticsServiceImpl implements StatisticsService {

	private final DcatServerRepository dcatServerRepository;
	private final MemberRepository memberRepository;
	private final EventStreamRepository eventStreamRepository;
	private final ViewRepository viewRepository;
	private final FragmentSequenceRepository fragmentSequenceRepository;

	public StatisticsServiceImpl(DcatServerRepository dcatServerRepository, MemberRepository memberRepository,
			EventStreamRepository eventStreamRepository, ViewRepository viewRepository,
			FragmentSequenceRepository fragmentSequenceRepository) {
		this.dcatServerRepository = dcatServerRepository;
		this.memberRepository = memberRepository;
		this.eventStreamRepository = eventStreamRepository;
		this.viewRepository = viewRepository;
		this.fragmentSequenceRepository = fragmentSequenceRepository;
	}

	@Override
	public JsonObject getMetrics() {
		JsonObject json = new JsonObject();
		dcatServerRepository.findSingleDcatServer()
				.ifPresent(dcatServer -> dcatServer.getDcat()
						.listStatements(null, createProperty(PROP_SERVERNAME), (RDFNode) null)
						.forEach(statement -> json.put(SERVERNAME + getLanguage(statement), statement.getString())));
		json.put(INGESTED_COUNT, memberRepository.getTotalSequence());
		json.put(CURRENT_COUNT, memberRepository.getMemberCount());

		JsonArray eventStreamJsons = new JsonArray();
		eventStreamRepository.retrieveAllEventStreams().stream().map(this::getLdesJson).forEach(eventStreamJsons::add);
		json.put(LDESES, eventStreamJsons);
		return json;
	}

	private String getLanguage(Statement statement) {
		return Objects.equals(statement.getLanguage(), "") ? "" : " " + statement.getLanguage();
	}

	private JsonObject getLdesJson(EventStream eventStream) {
		JsonObject ldesJson = new JsonObject();
		ldesJson.put(NAME, eventStream.getCollection());

		long ingestedMembers = memberRepository.getSequenceForCollection(eventStream.getCollection());
		ldesJson.put(INGESTED_COUNT, ingestedMembers);
		ldesJson.put(CURRENT_COUNT, memberRepository.getMemberCountOfCollection(eventStream.getCollection()));

		JsonArray viewJsons = new JsonArray();
		viewRepository.retrieveAllViewsOfCollection(eventStream.getCollection()).stream()
				.map(view -> getViewJson(view, ingestedMembers)).forEach(viewJsons::add);
		ldesJson.put(VIEWS, viewJsons);
		return ldesJson;
	}

	private JsonObject getViewJson(ViewSpecification view, long ingestedMembers) {
		JsonObject viewJson = new JsonObject();
		viewJson.put(NAME, view.getName().asString());

		JsonArray fragmentationJsons = new JsonArray();
		view.getFragmentations().stream().map(this::fragmentationConfigToJson).forEach(fragmentationJsons::add);
		viewJson.put(FRAGMENTATIONS, fragmentationJsons);

		viewJson.put(FRAGMENT_PROGRESS,
				JsonNumber.value(calculateFragmentationProgress(view.getName(), ingestedMembers)));
		return viewJson;
	}

	private double calculateFragmentationProgress(ViewName viewName, long ingestedMembers) {
		long lastProcessed = fragmentSequenceRepository.findLastProcessedSequence(viewName)
				.map(FragmentSequence::sequenceNr).orElse(0L);
		return ingestedMembers == 0 ? 1
				: 1 - (ingestedMembers - (double) lastProcessed) / ingestedMembers;
	}

	private JsonObject fragmentationConfigToJson(FragmentationConfig config) {
		JsonObject fragmentationJson = new JsonObject();
		fragmentationJson.put(NAME, config.getName());

		JsonObject propJsons = new JsonObject();
		config.getConfig().forEach(propJsons::put);
		fragmentationJson.put(PROPERTIES, propJsons);
		return fragmentationJson;
	}

}
