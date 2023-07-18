package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES;

public class RetentionPolicyConstants {

	private RetentionPolicyConstants() {
		// Class of constants
	}

	public static final String TIME_BASED_RETENTION_POLICY = LDES + "DurationAgoPolicy";
	public static final String VERSION_BASED_RETENTION_POLICY = LDES + "LatestVersionSubset";
	public static final String POINT_IN_TIME_RETENTION_POLICY = LDES + "PointInTimePolicy";
}
