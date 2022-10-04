package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config;

public class SubstringConfig {
	private String substringProperty;
	private Integer memberLimit;

	public String getSubstringProperty() {
		return substringProperty;
	}

	public void setSubstringProperty(String substringProperty) {
		this.substringProperty = substringProperty;
	}

	public void setMemberLimit(Integer memberLimit) {
		this.memberLimit = memberLimit;
	}

	public int getMemberLimit() {
		return memberLimit;
	}
}
