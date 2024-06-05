package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.MemberPaginationService;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.MemberPaginationServiceCreator;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PaginationProcessor implements ItemProcessor<List<BucketisedMember>, List<MemberAllocation>> {

	private final MemberPaginationServiceCreator memberPaginationServiceCreator;

	private final Map<String, MemberPaginationService> paginationServices = new HashMap<>();

	public PaginationProcessor(MemberPaginationServiceCreator memberPaginationServiceCreator) {
		this.memberPaginationServiceCreator = memberPaginationServiceCreator;
	}

	@Override
	public List<MemberAllocation> process(@NotNull List<BucketisedMember> bucketisedMembers) {
		if (bucketisedMembers.isEmpty()) {
			return null;
		}
		String viewName = bucketisedMembers.getFirst().getViewName();
		return paginationServices.get(viewName).paginateMember(bucketisedMembers);
	}

	@EventListener
	public void handleViewInitializationEvent(ViewInitializationEvent event) {
		paginationServices.put(event.getViewName().asString(), memberPaginationServiceCreator.createPaginationService(event.viewSpecification()));
	}

	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		paginationServices.put(event.getViewName().asString(), memberPaginationServiceCreator.createPaginationService(event.viewSpecification()));
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		paginationServices.remove(event.getViewName().asString());
	}
}