package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.collections.Prefixes;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PrefixAdderImplTest {
	private final Prefixes gipodPrefixes = () -> Map.of("gipod", "http://data.vlaanderen.be/ns/gipod#");
	private final Prefixes wellKnownPrefixes = () -> Map.of(
			"rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
			"dcterms", "http://purl.org/dc/terms/"
	);
	private final PrefixAdderImpl prefixAdder = new PrefixAdderImpl(List.of(gipodPrefixes, wellKnownPrefixes, Map::of));

	@Test
	void test_AddPrefixesToMap() {
		Model model = ModelFactory.createDefaultModel();

		final Map<String, String> nsPrefixMap = prefixAdder.addPrefixesToModel(model).getNsPrefixMap();

		assertThat(nsPrefixMap)
				.hasSize(3)
				.containsAllEntriesOf(gipodPrefixes.getPrefixes())
				.containsAllEntriesOf(wellKnownPrefixes.getPrefixes());
	}
}