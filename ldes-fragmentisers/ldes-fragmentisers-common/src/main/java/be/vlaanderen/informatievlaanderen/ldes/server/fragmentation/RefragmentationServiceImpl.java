package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MemberToFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.EventSourceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// TODO TVB: 28/07/23 test
@Service
public class RefragmentationServiceImpl implements RefragmentationService {

	private final EventSourceService eventSourceService;
	private final MemberToFragmentRepository memberToFragmentRepository;

	public RefragmentationServiceImpl(EventSourceService eventSourceService,
			MemberToFragmentRepository memberToFragmentRepository) {
		this.eventSourceService = eventSourceService;
		this.memberToFragmentRepository = memberToFragmentRepository;
	}

	@Override
	public void refragmentMembersForView(ViewName viewName,
			FragmentationStrategyExecutor fragmentationStrategyExecutor) {

		// TODO: 28/07/23 flow:
		// get


		AtomicInteger count = new AtomicInteger();
		eventSourceService
				.getMemberStreamOfCollection(viewName.getCollectionName())
				.forEach(ingestMember -> {
					final Member member = new Member(ingestMember.getId(), ingestMember.getModel(),
							ingestMember.getSequenceNr());
					memberToFragmentRepository.create(List.of(viewName), member);
					if (count.getAndIncrement() == 300_000) {
//						// TODO TVB: 28/07/23 we will need batch processing
//						// TODO TVB: 28/07/23 recovery?
		fragmentationStrategyExecutor.resume();

					}
				});

		// per 1.000 ophalen en fragmenteren

	}

}
