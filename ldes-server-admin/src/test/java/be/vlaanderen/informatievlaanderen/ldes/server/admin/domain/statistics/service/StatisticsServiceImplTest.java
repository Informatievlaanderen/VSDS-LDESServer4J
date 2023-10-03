package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.statistics.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
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
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.statistics.service.StatisticsConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatisticsServiceImplTest {
	private final DcatServerRepository dcatServerRepository = mock(DcatServerRepository.class);
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private final EventStreamRepository eventStreamRepository = mock(EventStreamRepository.class);
	private final ViewRepository viewRepository = mock(ViewRepository.class);
	private final FragmentSequenceRepository fragmentSequenceRepository = mock(FragmentSequenceRepository.class);

	private StatisticsServiceImpl statisticsService;

	@BeforeEach
	void setUp() {
		statisticsService = new StatisticsServiceImpl(dcatServerRepository, memberRepository, eventStreamRepository,
				viewRepository, fragmentSequenceRepository);
	}

	@Test
    void when_NoLdesPresent_Then_GetMetrics() throws URISyntaxException {
        when(eventStreamRepository.retrieveAllEventStreams()).thenReturn(List.of());
        Model serverDcat = readModelFromFile("statistics/server-dcat.ttl");
        when(dcatServerRepository.findSingleDcatServer()).thenReturn(Optional.of(new DcatServer("", serverDcat)));
        when(memberRepository.getTotalSequence()).thenReturn(100L);
        when(memberRepository.getMemberCount()).thenReturn(90L);

        JsonObject json = statisticsService.getMetrics();

        assertEquals(List.of(), json.get(LDESES));
        assertEquals('"'+"My LDES'es"+'"', json.get(SERVERNAME + " en").toString());
        assertEquals('"'+"Mijn LDESen"+'"', json.get(SERVERNAME + " nl").toString());
        assertEquals('"'+"LDES titel"+'"', json.get(SERVERNAME).toString());
        assertEquals("100", json.get(INGESTED_COUNT).toString());
        assertEquals("90", json.get(CURRENT_COUNT).toString());
    }

	@Test
	void when_LDESesWithNoViewsPresent_Then_GetMetrics() {
		String collectionName = "collection";
		String otherCollectionName = "otherCollection";
		EventStream ldes = new EventStream(collectionName, "timestamp", "version", "membertype");
		EventStream ldes2 = new EventStream(otherCollectionName, "timestamp", "version", "membertype");
		when(eventStreamRepository.retrieveAllEventStreams()).thenReturn(List.of(ldes, ldes2));

		when(viewRepository.retrieveAllViewsOfCollection(collectionName)).thenReturn(List.of());
		when(memberRepository.getSequenceForCollection(collectionName)).thenReturn(100L);
		when(memberRepository.getMemberCountOfCollection(collectionName)).thenReturn(90L);

		when(viewRepository.retrieveAllViewsOfCollection(otherCollectionName)).thenReturn(List.of());
		when(memberRepository.getSequenceForCollection(otherCollectionName)).thenReturn(80L);
		when(memberRepository.getMemberCountOfCollection(otherCollectionName)).thenReturn(40L);

		JsonObject json = statisticsService.getMetrics();

		JsonObject ldesJson = json.get(LDESES).getAsArray().get(0).getAsObject();
		assertEquals('"' + collectionName + '"', ldesJson.get(NAME).toString());
		assertEquals("100", ldesJson.get(INGESTED_COUNT).toString());
		assertEquals("90", ldesJson.get(CURRENT_COUNT).toString());
		assertEquals(List.of(), ldesJson.get(VIEWS));

		JsonObject ldes2Json = json.get(LDESES).getAsArray().get(1).getAsObject();
		assertEquals('"' + otherCollectionName + '"', ldes2Json.get(NAME).toString());
		assertEquals("80", ldes2Json.get(INGESTED_COUNT).toString());
		assertEquals("40", ldes2Json.get(CURRENT_COUNT).toString());
		assertEquals(List.of(), ldes2Json.get(VIEWS));
	}

	@Test
	void when_ViewsWithNoFragmentationsArePresent_Then_GetMetrics() {
		String collectionName = "collection";
		ViewName viewName = new ViewName(collectionName, "viewName");
		ViewName viewName2 = new ViewName(collectionName, "otherViewName");
		ViewName viewName3 = new ViewName(collectionName, "thirdViewName");
		EventStream ldes = new EventStream(collectionName, "timestamp", "version", "membertype");
		ViewSpecification view = new ViewSpecification(viewName, List.of(), List.of(), 100);
		ViewSpecification view2 = new ViewSpecification(viewName2, List.of(), List.of(), 100);
		ViewSpecification view3 = new ViewSpecification(viewName3, List.of(), List.of(), 100);

		when(eventStreamRepository.retrieveAllEventStreams()).thenReturn(List.of(ldes));
		when(viewRepository.retrieveAllViewsOfCollection(collectionName)).thenReturn(List.of(view, view2, view3));
		when(memberRepository.getSequenceForCollection(collectionName)).thenReturn(200L);
		when(fragmentSequenceRepository.findLastProcessedSequence(viewName)).thenReturn(Optional.empty());
		when(fragmentSequenceRepository.findLastProcessedSequence(viewName2))
				.thenReturn(Optional.of(new FragmentSequence(viewName2, 153L)));
		when(fragmentSequenceRepository.findLastProcessedSequence(viewName3))
				.thenReturn(Optional.of(new FragmentSequence(viewName3, 200L)));

		JsonObject json = statisticsService.getMetrics();

		JsonObject ldesJson = json.get(LDESES).getAsArray().get(0).getAsObject();
		JsonObject viewJson = ldesJson.get(VIEWS).getAsArray().get(0).getAsObject();
		assertEquals('"' + viewName.asString() + '"', viewJson.get(NAME).toString());
		assertEquals(List.of(), viewJson.get(FRAGMENTATIONS));
		assertEquals("0", viewJson.get(FRAGMENT_PROGRESS).toString());

		JsonObject view2Json = ldesJson.get(VIEWS).getAsArray().get(1).getAsObject();
		assertEquals('"' + viewName2.asString() + '"', view2Json.get(NAME).toString());
		assertEquals(List.of(), view2Json.get(FRAGMENTATIONS));
		assertEquals("77", view2Json.get(FRAGMENT_PROGRESS).toString());

		JsonObject view3Json = ldesJson.get(VIEWS).getAsArray().get(2).getAsObject();
		assertEquals('"' + viewName3.asString() + '"', view3Json.get(NAME).toString());
		assertEquals(List.of(), view3Json.get(FRAGMENTATIONS));
		assertEquals("100", view3Json.get(FRAGMENT_PROGRESS).toString());
	}

	@Test
	void when_ViewWithFragmentationsIsPresent_Then_GetMetrics() {
		String collectionName = "collection";
		ViewName viewName = new ViewName(collectionName, "viewName");
		String fragmentationName = "HierarchicalTimeBasedFragmentation";
		EventStream ldes = new EventStream(collectionName, "timestamp", "version", "membertype");
		FragmentationConfig fragmentation = new FragmentationConfig();
		fragmentation.setName(fragmentationName);
		Map<String, String> properties = Map.of("maxGranularity", "minute", "fragmentationPath",
				"http://www.w3.org/ns/prov#generatedAtTime");
		fragmentation.setConfig(properties);
		ViewSpecification view = new ViewSpecification(viewName, List.of(), List.of(fragmentation), 100);

		when(eventStreamRepository.retrieveAllEventStreams()).thenReturn(List.of(ldes));
		when(viewRepository.retrieveAllViewsOfCollection(collectionName)).thenReturn(List.of(view));
		when(memberRepository.getSequenceForCollection(collectionName)).thenReturn(200L);
		when(fragmentSequenceRepository.findLastProcessedSequence(viewName)).thenReturn(Optional.empty());

		JsonObject json = statisticsService.getMetrics();

		JsonObject ldesJson = json.get(LDESES).getAsArray().get(0).getAsObject();
		JsonObject viewJson = ldesJson.get(VIEWS).getAsArray().get(0).getAsObject();
		JsonObject fragmentationJson = viewJson.get(FRAGMENTATIONS).getAsArray().get(0).getAsObject();

		assertEquals('"' + fragmentationName + '"', fragmentationJson.get(NAME).toString());
		JsonObject propertiesJson = fragmentationJson.get(PROPERTIES).getAsObject();
		assertEquals('"' + "minute" + '"', propertiesJson.get("maxGranularity").toString());
		assertEquals('"' + "http://www.w3.org/ns/prov#generatedAtTime" + '"',
				propertiesJson.get("fragmentationPath").toString());
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}
}