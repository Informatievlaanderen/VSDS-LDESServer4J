package be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.exceptions.LdesFragmentIdentifierParseException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

public class LdesFragmentIdentifier {

	private final ViewName viewName;
	private final List<FragmentPair> fragmentPairs;

	public LdesFragmentIdentifier(ViewName viewName, List<FragmentPair> fragmentPairs) {
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
	}

	public LdesFragmentIdentifier(String viewName, List<FragmentPair> fragmentPairs) {
		this.viewName = ViewName.fromString(viewName);
		this.fragmentPairs = fragmentPairs;
	}

	public static LdesFragmentIdentifier fromFragmentId(String fragmentId) {
		try {
			String[] splitString = fragmentId.substring(1).split("\\?");
			String viewName = splitString[0];
			if (splitString.length > 1) {
				List<FragmentPair> fragmentPairs = new ArrayList<>();
				String[] fragmentPairStrings = splitString[1].split("&");
				for (String fragmentPairString : fragmentPairStrings) {
					String[] splitFragmentPairString = fragmentPairString.split("=", -1);
					fragmentPairs.add(new FragmentPair(splitFragmentPairString[0], splitFragmentPairString[1]));
				}
				return new LdesFragmentIdentifier(viewName, fragmentPairs);
			}
			return new LdesFragmentIdentifier(viewName, List.of());
		} catch (Exception e) {
			throw new LdesFragmentIdentifierParseException(fragmentId);
		}

	}

	public String asDecodedFragmentId() {
		return getFragmentId(false);
	}

	public String asEncodedFragmentId() {
		return getFragmentId(true);
	}

	private String getFragmentId(boolean encoded) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("/").append(viewName.asString());

		if (!fragmentPairs.isEmpty()) {
			stringBuilder.append("?");
			stringBuilder.append(fragmentPairs.stream()
					.map(fragmentPair -> encoded
							? fragmentPair.fragmentKey() + "=" + encode(fragmentPair.fragmentValue(), UTF_8)
							: fragmentPair.fragmentKey() + "=" + fragmentPair.fragmentValue())
					.collect(Collectors.joining("&")));
		}

		return stringBuilder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LdesFragmentIdentifier that = (LdesFragmentIdentifier) o;
		return Objects.equals(viewName, that.viewName) &&
				new HashSet<>(fragmentPairs).containsAll(that.fragmentPairs) &&
				new HashSet<>(that.fragmentPairs).containsAll(fragmentPairs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(viewName, fragmentPairs);
	}

	@Override
	public String toString() {
		return asDecodedFragmentId();
	}
}
