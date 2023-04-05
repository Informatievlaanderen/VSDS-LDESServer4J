package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config;

public class SubstringConfig {
	private String fragmenterSubjectFilter = ".*";
	private String fragmenterProperty;
	private Integer memberLimit;

	public String getFragmenterProperty() {
		return fragmenterProperty;
	}

	public String getFragmenterSubjectFilter() {
		return fragmenterSubjectFilter;
	}

	public void setFragmenterSubjectFilter(String fragmenterSubjectFilter) {
		this.fragmenterSubjectFilter = fragmenterSubjectFilter;
	}

	public void setFragmenterProperty(String fragmenterProperty) {
		this.fragmenterProperty = fragmenterProperty;
	}

	public void setMemberLimit(Integer memberLimit) {
		this.memberLimit = memberLimit;
	}

	public int getMemberLimit() {
		return memberLimit;
	}
}
