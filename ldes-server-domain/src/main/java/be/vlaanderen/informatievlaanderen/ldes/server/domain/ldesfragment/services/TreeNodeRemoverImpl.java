package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberUnallocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.TreeMemberRemover;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TreeNodeRemoverImpl implements TreeNodeRemover {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final MemberRepository memberRepository;

	private final TreeMemberRemover treeMemberRemover;

	public TreeNodeRemoverImpl(LdesFragmentRepository ldesFragmentRepository,
			MemberRepository memberRepository, TreeMemberRemover treeMemberRemover) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.memberRepository = memberRepository;
		this.treeMemberRemover = treeMemberRemover;
	}

	@Override
	public void removeLdesFragmentsOfView(ViewName viewName) {
		memberRepository.removeViewReferences(viewName);
		ldesFragmentRepository.removeLdesFragmentsOfView(viewName.asString());
	}

	@Override
	public void deleteTreeNodesByCollection(String collectionName) {
		ldesFragmentRepository.deleteTreeNodesByCollection(collectionName);
	}

	// start: migration code retention TODO remove
	@EventListener
	public void handleMemberUnallocatedEvent(MemberUnallocatedEvent event) {
		memberRepository.removeViewReferenceOfMember(event.memberId(), event.viewName());
		treeMemberRemover.deletingMemberFromCollection(event.memberId());
	}

	@EventListener
	public void handleMemberDeletedEvent(MemberDeletedEvent event) {
		treeMemberRemover.deletingMemberFromCollection(event.memberId());
	}
	// end: migration code retention TODO remove
}
