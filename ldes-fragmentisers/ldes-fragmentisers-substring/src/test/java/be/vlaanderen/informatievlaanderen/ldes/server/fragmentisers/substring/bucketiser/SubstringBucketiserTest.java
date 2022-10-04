package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.bucketiser;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubstringBucketiserTest {

	private SubstringBucketiser substringBucketiser;

	@BeforeEach
	void setUp() {
		SubstringConfig substringConfig = new SubstringConfig();
		substringConfig.setSubstringProperty("http://purl.org/dc/terms/description");
		substringBucketiser = new SubstringBucketiser(substringConfig);
	}

	@Test
	void when_propertyIsSet_SubstringBucketisingIsPossible() throws URISyntaxException, IOException {
		LdesMember ldesMember = readLdesMemberFromFile();

		List<String> buckets = substringBucketiser.bucketise(ldesMember);

		assertEquals(List.of("o", "om", "oms", "omsc", "omsch", "omschr", "omschri",
				"omschrij", "omschrijv", "omschrijvi", "omschrijvin", "omschrijving"), buckets);
	}

	private LdesMember readLdesMemberFromFile()
			throws URISyntaxException, IOException {
		File file = new File(
				Objects.requireNonNull(getClass().getClassLoader().getResource("example-ldes-member.nq")).toURI());

		return new LdesMember("a", RDFParserBuilder.create()
				.fromString(Files.lines(Paths.get(file.toURI())).collect(Collectors.joining())).lang(Lang.NQUADS)
				.toModel());
	}

}