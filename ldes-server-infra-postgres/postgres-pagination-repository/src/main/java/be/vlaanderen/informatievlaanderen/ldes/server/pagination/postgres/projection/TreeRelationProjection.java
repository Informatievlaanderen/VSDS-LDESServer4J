package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection;

import org.springframework.beans.factory.annotation.Value;

public interface TreeRelationProjection {
	@Value("#{target.toPage.partialUrl}")
	String getToPagePartialUrl();
	String getTreeRelationType();
	String getTreeValue();
	String getTreeValueType();
	String getTreePath();
}
