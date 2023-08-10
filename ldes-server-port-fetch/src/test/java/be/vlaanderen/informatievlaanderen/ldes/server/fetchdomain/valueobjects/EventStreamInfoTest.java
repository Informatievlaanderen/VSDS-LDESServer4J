package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.net.URISyntaxException;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventStreamInfoTest {

	private static final String HOST_NAME = "http://localhost:8080";
	private static final String COLLECTION_NAME = "mobility-hindrances";

	@ParameterizedTest
	@ArgumentsSource(EventStreamInfoArgumentProvider.class)
	void when_EventStreamInfoIsConvertedToStatements_then_ResultingModelIsAsExpected(boolean isView,
			String expectedModelFile) throws URISyntaxException {
		String eventStreamIdentifier = HOST_NAME + "/" + COLLECTION_NAME;
		String treeNodeIdentifier = HOST_NAME + "/" + COLLECTION_NAME + "/node";

		Model shacl = RDFParserBuilder.create().fromString(
				"""
						<http://localhost:8080/mobility-hindrances> <https://w3id.org/tree#shape> <http://localhost:8080/mobility-hindrances/shape>.""")
				.lang(Lang.NQUADS).toModel();
		Model dcat = RDFParserBuilder.create().fromString(
				"""
						<http://localhost:8080/mobility-hindrances> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#DataService>.""")
				.lang(Lang.NQUADS).toModel();
		EventStreamProperties eventStreamProperties = new EventStreamProperties(eventStreamIdentifier,
				"http://www.w3.org/ns/prov#generatedAtTime", "http://purl.org/dc/terms/isVersionOf");

		EventStreamInfo eventStreamInfo = new EventStreamInfo(treeNodeIdentifier, eventStreamIdentifier, shacl, isView,
				dcat.listStatements().toList(), eventStreamProperties);

		Model actualModel = ModelFactory.createDefaultModel().add(eventStreamInfo.convertToStatements());
		Model expectedModel = readModelFromFile(expectedModelFile);
		assertTrue(actualModel.isIsomorphicWith(expectedModel));
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}

	static class EventStreamInfoArgumentProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(false, "valueobjects/eventstreaminfo_not_view.ttl"),
					Arguments.of(true, "valueobjects/eventstreaminfo_view.ttl"));
		}
	}

}