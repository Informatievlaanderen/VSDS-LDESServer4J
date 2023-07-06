package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;

import java.util.Comparator;

public class ReverseTimeStampComparator implements Comparator<MemberProperties> {
	@Override
	public int compare(MemberProperties o1, MemberProperties o2) {
		return o2.getTimestamp().compareTo(o1.getTimestamp());
	}
}
