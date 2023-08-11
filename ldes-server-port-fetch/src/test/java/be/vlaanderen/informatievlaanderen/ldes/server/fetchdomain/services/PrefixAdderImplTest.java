package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.services;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PrefixAdderImplTest {
	PrefixAdder prefixAdder = new PrefixAdderImpl();

	@Test
	void when_PrefixesAndLocalNamesAreValid_TheyAreAddedToPrefixMap() throws URISyntaxException, IOException {
		Model model = readLdesMemberFromFile(getClass().getClassLoader(), "example.nq");
		Model updatedModel = prefixAdder.addPrefixesToModel(model);
		Map<String, String> nsPrefixMap = updatedModel.getNsPrefixMap();

		assertTrue(nsPrefixMap.containsKey("v1"));
		assertTrue(nsPrefixMap.containsKey("id"));
		assertTrue(nsPrefixMap.containsKey("rdf"));
	}

	private Model readLdesMemberFromFile(ClassLoader classLoader, String fileName)
			throws URISyntaxException, IOException {
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());

		return RDFParserBuilder.create()
				.fromString(Files.lines(Paths.get(file.toURI())).collect(Collectors.joining())).lang(Lang.NQUADS)
				.toModel();
	}
}