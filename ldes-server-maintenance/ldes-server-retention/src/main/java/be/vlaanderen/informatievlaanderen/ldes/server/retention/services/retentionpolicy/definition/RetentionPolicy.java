package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition;

import java.io.Serializable;

public interface RetentionPolicy extends Serializable {

	RetentionPolicyType getType();

}
