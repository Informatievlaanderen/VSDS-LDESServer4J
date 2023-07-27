package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.fragmentisers.FragmentiserConfigException;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.TimeBasedConstants.temporalFields;

public class TimeBasedConfig {
	private final String fragmenterSubjectFilter;
	private final String fragmentationPath;
	private final String maxGranularity;

	public TimeBasedConfig(String fragmenterSubjectFilter, String fragmentationPath, String maxGranularity) {
		this.fragmenterSubjectFilter = fragmenterSubjectFilter;
		this.fragmentationPath = fragmentationPath;
		if (temporalFields.contains(maxGranularity)) {
			this.maxGranularity = maxGranularity;
		} else {
			throw new FragmentiserConfigException(
					maxGranularity + " is not allowed. Allowed values are: "
							+ String.join(", ", temporalFields));
		}
	}

	public String getFragmentationPath() {
		return fragmentationPath;
	}

	public String getFragmenterSubjectFilter() {
		return fragmenterSubjectFilter;
	}

	public String getMaxGranularity() {
		return maxGranularity;
	}
}
