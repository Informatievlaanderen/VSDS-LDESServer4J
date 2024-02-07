package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelParserTest {
	private Model memberModel;
	private static final LocalDateTime time = LocalDateTime.of(2020, 12, 14, 13, 10, 1, 463000000);
	private static final String string = "2630 Aartselaar, Baron van Ertbornstraat: Werfzone";

	@BeforeEach
	void setUp() throws URISyntaxException {
		memberModel = readModelFromFile("example-ldes-member.nq");

	}

	@Test
	void when_MemberHasXMLDateTime_Then_ReturnLocalDateTime() {
		Optional<LocalDateTime> actual = ModelParser.getFragmentationObjectLocalDateTime(memberModel, ".*",
				"http://purl.org/dc/terms/created");

		assertThat(actual).hasValue(time);
	}

	@Test
	void when_MemberHasNoXMLDateTime_Then_ReturnLocalDateTime() throws URISyntaxException {
		String propertyString = "http://purl.org/dc/terms/created";
		memberModel.remove(memberModel.listStatements(null, createProperty(propertyString), (RDFNode) null).nextStatement());
		Optional<LocalDateTime> actual = ModelParser.getFragmentationObjectLocalDateTime(memberModel, ".*",
				propertyString);

		assertThat(actual).isEmpty();
	}

	@Test
	void when_MemberHasXMLString_Then_ReturnString() {
		Object actual = ModelParser.getFragmentationObject(memberModel, ".*", "http://purl.org/dc/terms/description");

		assertTrue(actual instanceof String);
		assertEquals(string, actual);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}
}