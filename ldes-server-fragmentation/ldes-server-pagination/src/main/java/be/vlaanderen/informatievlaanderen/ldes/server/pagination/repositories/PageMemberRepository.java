package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;

import java.util.List;

public interface PageMemberRepository {
	/**
	 * Retrieves a list of unpaginated member ids for a bucket
	 *
	 * @param bucketId Id of bucket
	 * @return List of unpaginated member ids for a bucket
	 */
	List<Long> getUnpaginatedMembersForBucket(long bucketId);

	/**
	 * Assigns Page Member to a Page
	 *
	 * @param openPage    Open page that will be used to assign members to
	 * @param pageMembers A list of member ids that will be assigned to a page
	 */
	void assignMembersToPage(Page openPage, List<Long> pageMembers);
}
