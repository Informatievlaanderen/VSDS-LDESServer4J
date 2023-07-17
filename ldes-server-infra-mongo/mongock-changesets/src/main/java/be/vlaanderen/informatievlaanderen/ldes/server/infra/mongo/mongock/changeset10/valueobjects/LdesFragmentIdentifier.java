package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.valueobjects;

import java.util.*;

public class LdesFragmentIdentifier {

	private final ViewName viewName;
	private final List<FragmentPair> fragmentPairs;

	public LdesFragmentIdentifier(String viewName, List<FragmentPair> fragmentPairs) {
		this.viewName = ViewName.fromString(viewName);
		this.fragmentPairs = fragmentPairs;
	}

	public ViewName getViewName() {
		return viewName;
	}

	public static LdesFragmentIdentifier fromFragmentId(String fragmentId) {
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
}
