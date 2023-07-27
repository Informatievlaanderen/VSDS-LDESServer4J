package be.vlaanderen.informatievlaanderen.ldes.server.domain;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

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
		LocalDateTime actual = ModelParser.getFragmentationObjectLocalDateTime(memberModel, ".*",
				"http://purl.org/dc/terms/created");

		assertEquals(time, actual);
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