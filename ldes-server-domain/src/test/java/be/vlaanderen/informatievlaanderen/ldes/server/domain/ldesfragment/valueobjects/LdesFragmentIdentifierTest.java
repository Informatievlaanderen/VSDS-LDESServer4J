package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.exceptions.LdesFragmentIdentifierParseException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LdesFragmentIdentifierTest {

	final String collectionName = "collection";
	final String viewName = "view";
	final String fullViewName = collectionName + "/" + viewName;

	final String fragmentPairKey1 = "key1";
	final String fragmentPairValue1 = "value1";
	final String fragmentPairKey2 = "key2";
	final String fragmentPairValue2 = "value2";

	final String fragmentIdString = "/" + fullViewName
			+ "?" + fragmentPairKey1 + "=" + fragmentPairValue1
			+ "&" + fragmentPairKey2 + "=" + fragmentPairValue2;
	final String rootIdString = "/" + fullViewName;
	final String malformedIdString = "/" + fullViewName
			+ "?faultyString"
			+ "&" + fragmentPairKey1 + "=" + fragmentPairValue1;
	List<FragmentPair> fragmentPairs;

	LdesFragmentIdentifier fragmentId;
	LdesFragmentIdentifier rootFragmentId;

	@BeforeEach
	void setUp() {
		fragmentPairs = List.of(new FragmentPair(fragmentPairKey1, fragmentPairValue1),
				new FragmentPair(fragmentPairKey2, fragmentPairValue2));
		fragmentId = new LdesFragmentIdentifier(fullViewName, fragmentPairs);
		rootFragmentId = new LdesFragmentIdentifier(fullViewName, List.of());
	}

	@Test
	void when_NonRootFragmentIdString_Then_CreateFragmentIdentifier() {
		assertEquals(fragmentId, LdesFragmentIdentifier.fromFragmentId(fragmentIdString));
	}

	@Test
	void when_RootFragmentIdString_Then_CreateFragmentIdentifier() {
		assertEquals(rootFragmentId, LdesFragmentIdentifier.fromFragmentId(rootIdString));
	}

	@Test
	void when_MalformedFragmentIdString_Then_CreateFragmentIdentifier() {
		assertThrows(LdesFragmentIdentifierParseException.class,
				() -> LdesFragmentIdentifier.fromFragmentId(malformedIdString),
				"LdesFragmentIdentifier could not be created from string: " + malformedIdString);
	}

	@Test
	void when_RootFragmentIdentifier_Then_CreateFragmentIdString() {
		assertEquals(rootIdString, rootFragmentId.asString());
	}

	@Test
	void when_NonRootFragmentIdentifier_Then_CreateFragmentIdString() {
		assertEquals(fragmentIdString, fragmentId.asString());
	}

	@Test
	void when_KeyPresent_Then_ReturnKey() {
		assertEquals(fragmentPairValue1, fragmentId.getValueOfFragmentPairKey(fragmentPairKey1).get());
	}

	@Test
	void when_KeyNotPresent_Then_ReturnEmptyOptional() {
		assertEquals(Optional.empty(), fragmentId.getValueOfFragmentPairKey("NotPresent"));
	}

}