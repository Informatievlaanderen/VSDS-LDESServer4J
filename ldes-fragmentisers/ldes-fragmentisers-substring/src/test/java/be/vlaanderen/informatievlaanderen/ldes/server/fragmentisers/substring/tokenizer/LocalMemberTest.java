package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.tokenizer;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model.LocalMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model.LocalMemberSupplier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model.SubstringToken;
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
import java.util.Set;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig.DEFAULT_FRAGMENTER_SUBJECT_FILTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalMemberTest {

	@Nested
	class GetTokens {

		private final SubstringConfig substringConfig = new SubstringConfig();
		private final LocalMemberSupplier localMemberSupplier = new LocalMemberSupplier(substringConfig);

		@BeforeEach
		void setUp() {
			substringConfig.setFragmenterProperty("http://purl.org/dc/terms/description");
			substringConfig.setFragmenterSubjectFilter(DEFAULT_FRAGMENTER_SUBJECT_FILTER);
		}

		@Test
		void shouldReturnEmpty_whenSubstringTargetNotFound() {
			Member member = new Member("memberId", ModelFactory.createDefaultModel(), new ArrayList<>());
			LocalMember localMember = localMemberSupplier.toLocalMember(member);

			Set<SubstringToken> tokens = localMember.getTokens();

			assertTrue(tokens.isEmpty());
		}

		@Test
		void shouldReturnOneNormalizedToken_whenSubstringTargetFoundWithoutSpaces()
				throws URISyntaxException, IOException {
			Member member = readLdesMemberFromFile("example-ldes-member.nq");
			LocalMember localMember = localMemberSupplier.toLocalMember(member);

			final Set<SubstringToken> tokens = localMember.getTokens();

			assertEquals(1, tokens.size());
			assertTrue(tokens.contains(new SubstringToken("omschrijving")));
		}

		@Test
		void shouldReturnThreeNormalizedTokens_whenSubstringTargetFoundWithOneSpaceChar()
				throws URISyntaxException, IOException {
			Member member = readLdesMemberFromFile("example-ldes-member-multiple-tokens.nq");
			LocalMember localMember = localMemberSupplier.toLocalMember(member);

			final Set<SubstringToken> tokens = localMember.getTokens();

			assertEquals(3, tokens.size());
			assertTrue(tokens.contains(new SubstringToken("dubbele omschrijving")));
			assertTrue(tokens.contains(new SubstringToken("dubbele")));
			assertTrue(tokens.contains(new SubstringToken("omschrijving")));
		}

		private Member readLdesMemberFromFile(String fileName)
				throws URISyntaxException, IOException {
			File file = new File(
					Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI());

			// noinspection resource
			return new Member("a", RDFParserBuilder.create()
					.fromString(Files.lines(Paths.get(file.toURI())).collect(Collectors.joining())).lang(Lang.NQUADS)
					.toModel(), List.of());
		}
	}

}