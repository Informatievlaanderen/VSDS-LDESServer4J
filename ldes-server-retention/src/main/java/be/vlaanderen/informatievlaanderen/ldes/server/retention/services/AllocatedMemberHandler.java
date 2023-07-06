package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberRepository;
import org.springframework.context.event.EventListener;

public class AllocatedMemberHandler {

    private final MemberRepository memberRepository;

    public AllocatedMemberHandler(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @EventListener
    public void handleEventMemberIngestedEvent(MemberAllocatedEvent event) {
        memberRepository.allocateMember(event.memberId(), event.viewName());
    }


}
