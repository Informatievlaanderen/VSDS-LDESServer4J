package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class UrlCharachterCodesConverter {

	public static String encode(String hostname, LdesFragmentIdentifier identifier) {
		List<FragmentPair> pairs = identifier.getFragmentPairs();
		String viewString = hostname + "/" + identifier.getViewName().asString();
		if (pairs.isEmpty()) {
			return viewString;
		}
		return pairs.stream()
				.map(pair -> {
					try {
						return pair.fragmentKey() + "=" + encodeValue(pair.fragmentValue());
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
				})
				.collect(joining("&", viewString + "?", ""));
	}

	public static String decode(String parameter) {
		return URLDecoder.decode(parameter, StandardCharsets.UTF_8);
	}

	private static String encodeValue(String value) throws UnsupportedEncodingException {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}
}
