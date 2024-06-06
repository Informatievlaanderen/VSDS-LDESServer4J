package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.ViewBucketisationService;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.MemberPaginationService;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.MemberPaginationServiceCreator;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class PaginationProcessor implements ItemProcessor<List<BucketisedMember>, List<MemberAllocation>> {

	private final MemberPaginationServiceCreator memberPaginationServiceCreator;
	private final ViewBucketisationService viewBucketisationService;
	protected final Map<String, MemberPaginationService> paginationServices = new HashMap<>();

	public PaginationProcessor(MemberPaginationServiceCreator memberPaginationServiceCreator, ViewBucketisationService viewBucketisationService) {
		this.memberPaginationServiceCreator = memberPaginationServiceCreator;
		this.viewBucketisationService = viewBucketisationService;
	}

	@Override
	public List<MemberAllocation> process(@NotNull List<BucketisedMember> bucketisedMembers) {
		if (bucketisedMembers.isEmpty()) {
			return null;
		}
		String viewName = bucketisedMembers.getFirst().getViewName();

		MemberPaginationService memberPaginationService = paginationServices.get(viewName);
		if (memberPaginationService == null) {
			throw new NoSuchElementException("View %s was not registered.".formatted(viewName));
		}
		return paginationServices.get(viewName).paginateMember(bucketisedMembers);
	}

	@EventListener({ViewAddedEvent.class, ViewInitializationEvent.class})
	public void handleViewInitializationEvent(ViewSupplier event) {
		paginationServices.put(event.viewSpecification().getName().asString(),
				memberPaginationServiceCreator.createPaginationService(event.viewSpecification()));
		viewBucketisationService.setPaginationHasView(event.viewSpecification().getName());
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		paginationServices.remove(event.getViewName().asString());
		viewBucketisationService.setPaginationHasDeletedView(event.getViewName());
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		paginationServices.keySet()
				.stream()
				.filter(viewName -> ViewName.fromString(viewName).getCollectionName().equals(event.collectionName()))
				.toList()
				.forEach(paginationServices::remove);
		viewBucketisationService.setPaginationHasDeletedCollection(event.collectionName());
	}

	protected Map<String, MemberPaginationService> getPaginationServices() {
		return paginationServices;
	}
}