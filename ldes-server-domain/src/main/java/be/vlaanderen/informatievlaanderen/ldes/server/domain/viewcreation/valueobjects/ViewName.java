package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ViewName implements Serializable {
	@Serial
	private static final long serialVersionUID = 1848404092753207934L;
	private final String name;
	private final String collectionName;

	public ViewName(String collectionName, String name) {
		this.collectionName = collectionName;
		this.name = name;
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
		if (splitViewName.length != 2) {
			throw new IllegalArgumentException(
					"Invalid full view name: %s. Exactly one '/' char expected.".formatted(viewName));
		}
		return new ViewName(splitViewName[0], splitViewName[1]);
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getCollectionIri(String hostName) {
		return hostName + "/" + getCollectionName();
	}

	public String getViewNameIri(String hostName) {
		return getCollectionIri(hostName) + "/" + getViewName();
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
