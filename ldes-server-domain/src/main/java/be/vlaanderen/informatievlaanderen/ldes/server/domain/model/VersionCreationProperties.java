package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import java.util.Objects;

public final class VersionCreationProperties {
	private final String versionDelimiter;

	private VersionCreationProperties(String versionDelimiter) {
		this.versionDelimiter = versionDelimiter;
	}

	public String getVersionDelimiter() {
		return versionDelimiter;
	}

	public boolean isVersionCreationEnabled() {
		return versionDelimiter != null;
	}

	public static VersionCreationProperties disabled() {
		return new VersionCreationProperties(null);
	}

	public static VersionCreationProperties enabledWithDefault() {
		return new VersionCreationProperties("/");
	}

	public static VersionCreationProperties ofNullableDelimiter(String versionDelimiter) {
		return new VersionCreationProperties(versionDelimiter);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof VersionCreationProperties that)) return false;
		return Objects.equals(versionDelimiter, that.versionDelimiter);
	}

	@Override
	public int hashCode() {
		return 17 * 31 + Objects.hashCode(versionDelimiter);
	}
}
