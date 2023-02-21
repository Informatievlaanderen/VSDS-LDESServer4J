package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.processor;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.constants.SubstringConstants.ROOT_SUBSTRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SubstringPreProcessorTest {

	private final SubstringConfig substringConfig = new SubstringConfig();
	private final SubstringPreProcessor substringPreProcessor = new SubstringPreProcessor(substringConfig);

	@Nested
	class GetSubstringTarget {

		@BeforeEach
		void setUp() {
			substringConfig.setFragmenterProperty("http://purl.org/dc/terms/description");
		}

		@Test
		void shoudReturnValueFromFragmenterProperty_whenPresent() throws URISyntaxException, IOException {
			final Member member = readLdesMemberFromFile();

			String substringTarget = substringPreProcessor.getSubstringTarget(member);

			assertEquals(substringTarget, "omschríjvińĝ");
		}

		@Test
		void shouldReturnNull_whenFragmenterPropertyNotPresentOnMember() {
			Member member = new Member("id", ModelFactory.createDefaultModel(), new ArrayList<>());
			assertNull(substringPreProcessor.getSubstringTarget(member));
		}

		private Member readLdesMemberFromFile()
				throws URISyntaxException, IOException {
			File file = new File(
					Objects.requireNonNull(getClass().getClassLoader().getResource("example-ldes-member.nq")).toURI());

			// noinspection resource
			return new Member("a", RDFParserBuilder.create()
					.fromString(Files.lines(Paths.get(file.toURI())).collect(Collectors.joining())).lang(Lang.NQUADS)
					.toModel(), List.of());
		}

	}

	@Nested
	class Bucketize {

		@Test
		void shouldReturnStringInBuckets_whenStringNotNull() {
			List<String> buckets = substringPreProcessor.bucketize("omschrijving");

			assertEquals(List.of("", "o", "om", "oms", "omsc", "omsch", "omschr", "omschri",
					"omschrij", "omschrijv", "omschrijvi", "omschrijvin", "omschrijving"),
					buckets);
		}

		@Test
		void shouldReturnSingletonListWithRoot_whenStringNull() {
			assertEquals(List.of(ROOT_SUBSTRING), substringPreProcessor.bucketize(null));
		}

	}

	@Nested
	class Tokenize {

		@Test
		void shouldTokenizeStringOnSpaceCharacter_whenStringContainsSpaces() {
			assertEquals(List.of("two", "parts", "two parts"), substringPreProcessor.tokenize("two parts"));
		}

		@Test
		void shouldContainSingletonList_whenStringContainsNoSpaces() {
			final String subString = "one_part";
			assertEquals(List.of(subString), substringPreProcessor.tokenize(subString));
		}

		@Test
		void shouldReturnEmpty_whenStringNull() {
			assertEquals(0, substringPreProcessor.tokenize(null).size());
		}

		@Test
		void shouldNormalizeString_whenStringIsNotNormalized() {
			assertEquals("omschrijving", substringPreProcessor.tokenize("omschríjvińĝ").get(0));
		}
	}

}