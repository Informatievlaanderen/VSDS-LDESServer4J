package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config;

public class SubstringConfig {
	private String fragmenterPropertyQuery;
	private Integer memberLimit;

	public String getFragmenterPropertyQuery() {
		return fragmenterPropertyQuery;
	}

	public void setFragmenterPropertyQuery(String fragmenterPropertyQuery) {
		this.fragmenterPropertyQuery = fragmenterPropertyQuery;
	}

	public void setMemberLimit(Integer memberLimit) {
		this.memberLimit = memberLimit;
	}

	public int getMemberLimit() {
		return memberLimit;
	}
}
