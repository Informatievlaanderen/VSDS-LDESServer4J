package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document("fragmentation_allocation")
public class AllocationEntity {
	@Id
	private final AllocationKey allocationKey;
	@Indexed
	private final ViewName viewName;

	public AllocationEntity(AllocationKey allocationKey, ViewName viewName) {
		this.allocationKey = allocationKey;
		this.viewName = viewName;
	}

	public AllocationKey getAllocationKey() {
		return allocationKey;
	}

	public ViewName getViewName() {
		return viewName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AllocationEntity that = (AllocationEntity) o;
		return Objects.equals(allocationKey, that.allocationKey) && Objects.equals(viewName,
				that.viewName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(allocationKey, viewName);
	}

	public record AllocationKey(String memberId, String fragmentId) {
	}

}
