package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UrlCharachterCodesConverterTest {

	@Test
	void when_ViewHasNoFragmentPairs_Then_ReturnAsString() {
		LdesFragmentIdentifier toEncode = new LdesFragmentIdentifier(new ViewName("collection", "view"),
				List.of());

		String actual = UrlCharachterCodesConverter.encode("hostname", toEncode);

		assertEquals("hostname/collection/view", actual);
	}

	@Test
	void when_ViewnameContainsSpecialCharacters_Then_CharactersAreEncoded() {
		LdesFragmentIdentifier toEncode = new LdesFragmentIdentifier(new ViewName("collection", "view"),
				List.of(new FragmentPair("key1", "value="), new FragmentPair("key2", "%$&")));

		String actual = UrlCharachterCodesConverter.encode("hostname", toEncode);

		assertEquals("hostname/collection/view?key1=value%3D&key2=%25%24%26", actual);
	}

	@Test
	void when_URIContainsSpecialCharacters_Then_CharactersAreDecoded() {
		String toDecode = "%25%24%26";

		String actual = UrlCharachterCodesConverter.decode(toDecode);

		assertEquals("%$&", actual);
	}

}