package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config;

public class SubstringConfig {

	public static final String DEFAULT_FRAGMENTER_SUBJECT_FILTER = ".*";

	private String fragmenterSubjectFilter = DEFAULT_FRAGMENTER_SUBJECT_FILTER;
	private String fragmentationPath;
	private Integer memberLimit;

	public String getFragmentationPath() {
		return fragmentationPath;
	}

	public String getFragmenterSubjectFilter() {
		return fragmenterSubjectFilter;
	}

	public void setFragmenterSubjectFilter(String fragmenterSubjectFilter) {
		this.fragmenterSubjectFilter = fragmenterSubjectFilter;
	}

	public void setFragmenterPath(String fragmentationPath) {
		this.fragmentationPath = fragmentationPath;
	}

	public void setMemberLimit(Integer memberLimit) {
		this.memberLimit = memberLimit;
	}

	public int getMemberLimit() {
		return memberLimit;
	}
}
