package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.EventStreamProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FragmentationMemberTest {
	private static final String DC_TERMS = "http://purl.org/dc/terms/";
	private static final String COLLECTION_NAME = "collection";
	private static final String TIMESTAMP_PATH = DC_TERMS + "created";
	private static final String VERSION_OF_PATH = DC_TERMS + "isVersionOf";
	private static final String SUBJECT = "http://temporary.org#bart/2024-07-26T12:29:02.343350100";
	private static final String IS_VERSION_OF = "http://temporary.org#bart";
	private static final LocalDateTime TIMESTAMP = LocalDateTime.parse("2024-07-26T12:29:02.343350100");

	@Test
	void given_versionObject_test_GetVersionModel() {
		final EventStreamProperties eventStreamProperties = new EventStreamProperties(COLLECTION_NAME, VERSION_OF_PATH, TIMESTAMP_PATH, false);
		final Model model = RDFParser.source("version-member.ttl").toModel();
		final FragmentationMember fragmentationMember = new FragmentationMember(1, SUBJECT, IS_VERSION_OF, TIMESTAMP, eventStreamProperties, model  );

		final Model actual = fragmentationMember.getVersionModel();

		assertThat(actual).matches(model::isIsomorphicWith);
	}

	@Test
	void given_stateObject_test_GetVersionModel() {
		final EventStreamProperties eventStreamProperties = new EventStreamProperties(COLLECTION_NAME, VERSION_OF_PATH, TIMESTAMP_PATH, false);
		final Model model = RDFParser.fromString("<http://temporary.org#bart> <http://example.com/vocab#name> \"Bart\" .").lang(Lang.TTL).toModel();
		final FragmentationMember fragmentationMember = new FragmentationMember(1, SUBJECT, IS_VERSION_OF, TIMESTAMP, eventStreamProperties, model  );

		final Model actual = fragmentationMember.getVersionModel();

		assertThat(actual).matches(model::isIsomorphicWith);
	}
}