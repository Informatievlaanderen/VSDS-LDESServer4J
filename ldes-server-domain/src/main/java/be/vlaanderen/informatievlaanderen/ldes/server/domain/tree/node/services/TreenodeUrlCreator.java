package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class TreenodeUrlCreator {
	private TreenodeUrlCreator() {
	}

	public static String encode(String hostname, LdesFragmentIdentifier identifier) {
		List<FragmentPair> pairs = identifier.getFragmentPairs();
		String viewString = hostname + "/" + identifier.getViewName().asString();
		if (pairs.isEmpty()) {
			return viewString;
		}
		return pairs.stream()
				.map(pair -> pair.fragmentKey() + "=" + encodeValue(pair.fragmentValue()))
				.collect(joining("&", viewString + "?", ""));
	}

	public static String decode(String parameter) {
		return URLDecoder.decode(parameter, StandardCharsets.UTF_8);
	}

	private static String encodeValue(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}
}
