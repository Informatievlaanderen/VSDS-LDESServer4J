package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

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
			Set<Token> tokens = fragmentationString.getTokens(false);

			assertTrue(tokens.isEmpty());
		}

		@ParameterizedTest
		@ArgumentsSource(TokenArgumentProvider.class)
		void shouldReturnOneNormalizedTokens_whenFragmentationStringDoesNotContainSpaces(String input,
				boolean caseSensitive, Set<Token> expectedTokens) {
			FragmentationString fragmentationString = new FragmentationString(input);

			Set<Token> tokens = fragmentationString.getTokens(caseSensitive);

			assertEquals(expectedTokens, tokens);
		}

		static class TokenArgumentProvider implements ArgumentsProvider {
			@Override
			public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
				return Stream.of(
						Arguments.of("DeStràAtMétVEèlTökEnS", false, Stream.of("destraatmetveeltokens")
								.map(Token::new)
								.collect(Collectors.toSet())),
						Arguments.of("DeStràAtMétVEèlTökEnS", true, Stream.of("DeStraAtMetVEelTokEnS")
								.map(Token::new)
								.collect(Collectors.toSet())),
						Arguments.of("De StràAt Mét VEèl TökEnS", false,
								Stream.of("de", "straat", "met", "veel", "tokens")
										.map(Token::new)
										.collect(Collectors.toSet())),
						Arguments.of("De StràAt Mét VEèl TökEnS", true,
								Stream.of("De", "StraAt", "Met", "VEel", "TokEnS")
										.map(Token::new)
										.collect(Collectors.toSet())));
			}
		}
	}

}