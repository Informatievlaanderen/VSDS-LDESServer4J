package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.services.TreenodeUrlDecoder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TreenodeUrlDecoderTest {
	@ParameterizedTest
	@CsvSource({ "%25%24%26,%$&",
			"http://localhost:8080/kbo/by-time?created=2023-07-20T15%3A41%3A43.215Z,http://localhost:8080/kbo/by-time?created=2023-07-20T15:41:43.215Z" })
	void when_URIContainsSpecialCharacters_Then_CharactersAreDecoded(String input, String expected) {
		String actual = TreenodeUrlDecoder.decode(input);

		assertEquals(expected, actual);
	}
}
