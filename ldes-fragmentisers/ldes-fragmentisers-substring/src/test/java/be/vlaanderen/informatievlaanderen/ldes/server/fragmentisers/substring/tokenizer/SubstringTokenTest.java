package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.tokenizer;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model.SubstringToken;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.SubstringFragmentationStrategy.ROOT_SUBSTRING;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubstringTokenTest {

	@Nested
	class GetBuckets {

		@Test
		void shouldContainBucketOfSubstringsAndRootSubstring_whenTokenIsNotBlank() {
			SubstringToken token = new SubstringToken("ab");
			List<String> bucket = token.getBucket();

			assertTrue(bucket.contains(ROOT_SUBSTRING));
			assertTrue(bucket.contains("a"));
			assertTrue(bucket.contains("ab"));
		}

		@Test
		void shouldContainRootSubstring_whenTokenIsBlank() {
			SubstringToken token = new SubstringToken(null);
			List<String> bucket = token.getBucket();

			assertTrue(bucket.contains(ROOT_SUBSTRING));
		}
	}

}