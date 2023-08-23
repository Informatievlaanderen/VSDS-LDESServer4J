package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.tokenizer;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model.Token;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.SubstringFragmentationStrategy.ROOT_SUBSTRING;
import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

	@Nested
	class GetBuckets {

		@Test
		void shouldContainBucketOfSubstringsAndRootSubstring_whenTokenIsNotBlank() {
			Token token = new Token("ab");
			List<String> bucket = token.getBuckets();

			assertTrue(bucket.contains(ROOT_SUBSTRING));
			assertTrue(bucket.contains("a"));
			assertTrue(bucket.contains("ab"));
		}

		@Test
		void shouldContainRootSubstring_whenTokenIsBlank() {
			Token token = new Token(null);
			List<String> bucket = token.getBuckets();

			assertTrue(bucket.contains(ROOT_SUBSTRING));
		}
	}

	@Nested
	class Equality {

		@Test
		void test_EqualityOfToken() {
			Token token = new Token("token");
			Token otherToken = new Token("token");
			assertEquals(token, otherToken);
			assertEquals(token, token);
			assertEquals(otherToken, otherToken);
		}

		@ParameterizedTest
		@ArgumentsSource(TokenArgumentsProvider.class)
		void test_InequalityOfToken(Object otherToken) {
			Token token = new Token("token");
			assertNotEquals(token, otherToken);
		}

		static class TokenArgumentsProvider implements ArgumentsProvider {

			@Override
			public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
				return Stream.of(
						Arguments.of(new Token("otherToken")),
						Arguments.of(new FragmentPair("a", "b")),
						Arguments.of((Object) null));
			}
		}
	}

}