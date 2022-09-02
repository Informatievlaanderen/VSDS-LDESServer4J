package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import java.util.Objects;

public class FragmentationSpecification {
	private final String name;
	private final FragmentationProperties properties;

	public FragmentationSpecification(String name, FragmentationProperties properties) {
		this.name = name;
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public FragmentationProperties getProperties() {
		return properties;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FragmentationSpecification that = (FragmentationSpecification) o;
		return Objects.equals(name, that.name) && Objects.equals(properties, that.properties);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, properties);
	}
}
