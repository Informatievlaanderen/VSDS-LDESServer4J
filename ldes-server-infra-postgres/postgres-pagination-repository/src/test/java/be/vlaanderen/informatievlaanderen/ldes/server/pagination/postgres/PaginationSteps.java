package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.BucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

public class PaginationSteps extends PostgresPaginationIntegrationTest {
	private List<Long> memberIds;
	private int pageCount;
	private Long bucketId;
	private Integer viewId;
	private Page openPage;

	@Before
	public void setUp() {
		pageCount = pageEntityRepository.findAll().size();
		final BucketEntity bucket = bucketEntityRepository.findAll().getFirst();
		bucketId = bucket.getBucketId();
		viewId = bucket.getView().getId();
		openPage = pageRepository.getOpenPage(bucketId);
	}

	@Given("I have {int} unpaged members in one bucket")
	public void iHaveUnpagedMembersForBucket(int memberCount) {
		memberIds = LongStream.rangeClosed(1, memberCount).boxed().toList();

		saveMembers(memberIds);
		saveMemberBuckets(memberIds);
	}

	@When("I assign the members to the page")
	public void iAssignTheMembersToThePage() {
		pageMemberRepository.assignMembersToPage(openPage, memberIds);
	}

	@Then("I expect a new page is created")
	public void iExpectANewPageIsCreated() {
		var currentPageCount = pageEntityRepository.count();
		assertThat(currentPageCount).isGreaterThan(pageCount);
	}

	@Then("the open page has space for {int} more members")
	public void theOpenPageContainsMembers(int memberCount) {
		var page = pageRepository.getOpenPage(bucketId);
		assertThat(page.getAvailableMemberSpace()).isEqualTo(memberCount);
	}

	@Then("I expect no more unpaged members")
	public void iExpectNoMoreUnpagedMembers() {
		var unpagedMembersCount = pageMemberEntityRepository.findAll()
				.stream()
				.filter(entity -> entity.getBucket() == null)
				.count();
		assertThat(unpagedMembersCount).isZero();
	}

	@When("I create a page given the mutable page")
	public void iCreateAPageGivenTheMutablePage() {
		openPage = pageRepository.getOpenPage(bucketId);
		pageRepository.createNextPage(openPage);
	}

	@And("The old page has a generic relation to the new page")
	public void theOldPageHasAGenericRelationToTheNewPage() {
		var oldOpenPageId = openPage.getId();
		openPage = pageRepository.getOpenPage(bucketId);
		var newOpenPageId = openPage.getId();

		var relationPresent = pageRelationEntityRepository.findAll()
				.stream()
				.anyMatch(pageRelationEntity ->
						pageRelationEntity.getRelationId().getFromPageId().equals(oldOpenPageId) &&
						pageRelationEntity.getRelationId().getToPageId().equals(newOpenPageId) &&
						pageRelationEntity.getTreeRelationType().equals("https://w3id.org/tree#Relation"));

		assertThat(relationPresent).isTrue();
	}

	private void saveMembers(List<Long> memberIds) {
		var collectionId = eventStreamEntityRepository.findAll().getFirst().getId();
		String eventStream = "http://example.com/es";
		String sql = "INSERT INTO members (subject, collection_id, version_of, timestamp, transaction_id, member_model) VALUES (?,?,?,?,?,?)";


		final List<Object[]> batchArgs = memberIds.stream()
				.map(member -> new Object[]{
						"http://example.com/es/%d".formatted(member), collectionId, eventStream,
						LocalDateTime.now(), 0, new byte[]{}
				})
				.toList();

		jdbcTemplate.batchUpdate(sql, batchArgs);
	}

	private void saveMemberBuckets(List<Long> memberIds) {

		String sql = "insert into page_members (bucket_id, member_id, view_id) VALUES (?, ?, ?)";

		final List<Object[]> batchArgs = memberIds.stream()
				.map(member -> new Object[]{
						bucketId, member, viewId
				})
				.toList();

		jdbcTemplate.batchUpdate(sql, batchArgs);
	}

}
