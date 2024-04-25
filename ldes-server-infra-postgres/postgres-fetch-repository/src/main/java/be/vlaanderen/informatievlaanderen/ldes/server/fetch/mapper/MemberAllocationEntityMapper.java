package be.vlaanderen.informatievlaanderen.ldes.server.fetch.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;

public class MemberAllocationEntityMapper {

	public MemberAllocation toMemberAllocation(MemberAllocationEntity memberAllocationEntity) {
		return new MemberAllocation(memberAllocationEntity.getId(), memberAllocationEntity.getCollectionName(),
				memberAllocationEntity.getViewName(), memberAllocationEntity.getFragmentId(),
				memberAllocationEntity.getMemberId());
	}

	public MemberAllocationEntity toMemberAllocationEntity(MemberAllocation memberAllocation) {
		return new MemberAllocationEntity(memberAllocation.getId(), memberAllocation.getCollectionName(),
				memberAllocation.getViewName(), memberAllocation.getFragmentId(), memberAllocation.getMemberId());
	}
}
