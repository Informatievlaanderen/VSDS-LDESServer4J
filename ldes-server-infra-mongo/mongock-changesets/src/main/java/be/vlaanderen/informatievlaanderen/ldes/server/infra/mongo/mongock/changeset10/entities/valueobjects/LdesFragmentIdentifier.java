package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesFragmentIdentifierParseException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.*;
import java.util.stream.Collectors;

public class LdesFragmentIdentifier {

	private final ViewName viewName;
	private final List<be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair> fragmentPairs;

	@PersistenceCreator
	public LdesFragmentIdentifier(ViewName viewName, List<be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair> fragmentPairs) {
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
	}

	public LdesFragmentIdentifier(String viewName, List<be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair> fragmentPairs) {
		this.viewName = ViewName.fromString(viewName);
		this.fragmentPairs = fragmentPairs;
	}

	public ViewName getViewName() {
		return viewName;
	}

	public List<be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair> getFragmentPairs() {
		return fragmentPairs;
	}

	public Optional<String> getValueOfFragmentPairKey(String key) {
		return fragmentPairs.stream().filter(pair -> pair.fragmentKey().equals(key))
				.map(be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair::fragmentValue)
				.findFirst();
	}

	public static LdesFragmentIdentifier fromFragmentId(String fragmentId) {
		try {
			String[] splitString = fragmentId.substring(1).split("\\?");
			String viewName = splitString[0];
			if (splitString.length > 1) {
				List<be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair> fragmentPairs = new ArrayList<>();
				String[] fragmentPairStrings = splitString[1].split("&");
				for (String fragmentPairString : fragmentPairStrings) {
					String[] splitFragmentPairString = fragmentPairString.split("=", -1);
					fragmentPairs.add(new be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair(splitFragmentPairString[0], splitFragmentPairString[1]));
				}
				return new LdesFragmentIdentifier(viewName, fragmentPairs);
			}
			return new LdesFragmentIdentifier(viewName, List.of());
		} catch (Exception e) {
			throw new LdesFragmentIdentifierParseException(fragmentId);
		}

	}

	public String asString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("/").append(viewName.asString());

		if (!fragmentPairs.isEmpty()) {
			stringBuilder.append("?");
			stringBuilder.append(fragmentPairs.stream()
					.map(fragmentPair -> fragmentPair.fragmentKey() + "=" + fragmentPair.fragmentValue())
					.collect(Collectors.joining("&")));
		}

		return stringBuilder.toString();
	}

	public Optional<LdesFragmentIdentifier> getParentId() {

		if (!this.fragmentPairs.isEmpty()) {
			List<FragmentPair> parentPairs = new ArrayList<>(fragmentPairs);
			parentPairs.remove(parentPairs.size() - 1);

			return Optional.of(new LdesFragmentIdentifier(viewName, parentPairs));
		}
		return Optional.empty();
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
