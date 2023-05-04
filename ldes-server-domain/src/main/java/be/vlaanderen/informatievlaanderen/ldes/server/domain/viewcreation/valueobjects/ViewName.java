package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import java.util.Objects;

public class ViewName {

	private final String name;
	private final String collectionName;

	public ViewName(String collectionName, String name) {
		this.name = name;
		this.collectionName = collectionName;
	}

	public ViewName withCollectionName(String collectionName) {
		return new ViewName(collectionName, name);
	}

	public static ViewName fromString(String viewName) {
		if (!viewName.contains("/")) {
			throw new IllegalArgumentException(
					"Invalid full view name: %s. '/' char expected after collectionName.".formatted(viewName));
		}

		final String[] splitViewName = viewName.split("/");
		return new ViewName(splitViewName[0], splitViewName[1]);
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getViewName() {
		return name;
	}

	public String asString() {
		return collectionName + "/" + name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ViewName viewName = (ViewName) o;
		return Objects.equals(name, viewName.name) && Objects.equals(collectionName, viewName.collectionName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, collectionName);
	}

}
