package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TreenodeUrlDecoderTest {
	@Test
	void when_URIContainsSpecialCharacters_Then_CharactersAreDecoded() {
		String toDecode = "%25%24%26";

		String actual = TreenodeUrlDecoder.decode(toDecode);

		Assertions.assertEquals("%$&", actual);
	}
}
