package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.bucketisedmember;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.PostgresFragmentationIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BucketRepositorySteps extends PostgresFragmentationIntegrationTest {

    private List<BucketisedMember> members;

    @DataTableType
    public BucketisedMember bucketisedMemberEntryTransformer(Map<String, String> row) {
        return new BucketisedMember(row.get("memberId"), ViewName.fromString(row.get("viewName")),
                row.get("fragmentId"), Long.parseLong(row.get("sequenceNr")));
    }

    @Before
    public void setup() {
        members = List.of();
        bucketisedMemberRepository.deleteByCollection("mobility-hindrances");
        bucketisedMemberRepository.deleteByCollection("parcels");
    }

    @Given("The following bucketisedMembers")
    public void theFollowingMembers(List<BucketisedMember> members) {
        this.members = members;
    }

    @When("I save the bucketisedMembers using the BucketisedMemberRepository")
    public void iSaveTheMembers() {
        members.stream()
                .map(mapper::toMemberBucketisationEntity)
                .forEach(memberBucketJpaRepository::save);
    }

    @When("I delete the members of collection {string}")
    public void iDeleteBucketisedMembersOfTheCollection(String collectionName) {
        bucketisedMemberRepository.deleteByCollection(collectionName);
    }

    @When("I delete the members of view {string}")
    public void iDeleteBucketisedMembersOfTheView(String viewName) {
        bucketisedMemberRepository.deleteByViewName(ViewName.fromString(viewName));
    }

    @Then("The BucketisedMemberRepository contains all the members")
    public void membersArePresent() {
        members.forEach(bucketisedMember -> {
            assertTrue(memberBucketEntityRepository
                    .findUnprocessedBuckets(bucketisedMember.getViewName(), bucketisedMember.viewName().getCollectionName(), Pageable.ofSize(1000)).isEmpty());
        });
    }

    @Then("The BucketisedMemberRepository does not contain the members of collection {string}")
    public void membersOfCollectionAreNotPresent(String collection) {
        members.forEach(bucketisedMember -> {
            assertTrue(memberBucketEntityRepository
                    .findUnprocessedBuckets(bucketisedMember.getViewName(), collection, Pageable.ofSize(1000)).isEmpty());
        });
    }

    @Then("The BucketisedMemberRepository does not contain the members of view {string}")
    public void membersAreNotPresent(String viewName) {
        var view = ViewName.fromString(viewName);
        assertTrue(memberBucketEntityRepository
                .findUnprocessedBuckets(view.getViewName(), view.getCollectionName(), Pageable.ofSize(1000)).isEmpty());
    }
}
