package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FragmentationStringTest {

	@Nested
	class GetTokens {
		@Test
		void shouldReturnEmpty_whenFragmentationStringIsEmptyString() {
			FragmentationString fragmentationString = new FragmentationString("");
			Set<Token> tokens = fragmentationString.getTokens();

			assertTrue(tokens.isEmpty());
		}

		@Test
		void shouldReturnOneNormalizedTokens_whenFragmentationStringDoesNotContainSpaces() {
			FragmentationString fragmentationString = new FragmentationString("DeStràAtMétVEèlTökEnS");
			Set<Token> tokens = fragmentationString.getTokens();

			Set<Token> expectedTokens = Stream.of("destraatmetveeltokens")
					.map(Token::new)
					.collect(Collectors.toSet());
			assertEquals(expectedTokens, tokens);
		}

		@Test
		void shouldReturnMultipleNormalizedTokens_whenFragmentationStringConstainsSpaces() {
			FragmentationString fragmentationString = new FragmentationString("De StràAt Mét VEèl TökEnS");
			Set<Token> tokens = fragmentationString.getTokens();

			Set<Token> expectedTokens = Stream.of("de", "straat", "met", "veel", "tokens")
					.map(Token::new)
					.collect(Collectors.toSet());
			assertEquals(expectedTokens, tokens);
		}
	}
}