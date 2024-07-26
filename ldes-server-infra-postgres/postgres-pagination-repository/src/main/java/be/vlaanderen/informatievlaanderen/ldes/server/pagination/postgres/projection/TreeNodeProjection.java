package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface TreeNodeProjection {
	long getId();
	String getPartialUrl();
	boolean isImmutable();
	LocalDateTime getNextUpdateTs();
	@Value("#{target.bucket.view.name}")
	String getViewName();
	@Value("#{target.bucket.view.eventStream.name}")
	String getCollectionName();
	boolean isView();
}
