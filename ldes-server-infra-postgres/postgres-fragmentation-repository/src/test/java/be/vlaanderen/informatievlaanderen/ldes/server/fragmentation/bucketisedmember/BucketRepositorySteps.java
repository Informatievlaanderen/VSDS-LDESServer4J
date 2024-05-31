package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.bucketisedmember;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.PostgresFragmentationIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

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
        bucketisedMemberRepository.insertAll(members);
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
            assertFalse(
                    bucketisedMemberRepository
                            .getFirstUnallocatedMember(bucketisedMember.viewName(), bucketisedMember.sequenceNr()).isEmpty());
        });
        bucketisedMemberRepository.insertAll(members);
    }

    @Then("The BucketisedMemberRepository does not contain the members of collection {string}")
    public void membersOfCollectionAreNotPresent(String collection) {
        members.forEach(bucketisedMember -> {
            assertFalse(
                    bucketisedMember.viewName().getCollectionName().equals(collection) ^
                            bucketisedMemberRepository
                                    .getFirstUnallocatedMember(bucketisedMember.viewName(), bucketisedMember.sequenceNr()).isEmpty());
        });
        bucketisedMemberRepository.insertAll(members);
    }

    @Then("The BucketisedMemberRepository does not contain the members of view {string}")
    public void membersAreNotPresent(String viewName) {
        members.forEach(bucketisedMember -> {
            assertFalse(
                    bucketisedMember.viewName().asString().equals(viewName) ^
                    bucketisedMemberRepository
                            .getFirstUnallocatedMember(bucketisedMember.viewName(), bucketisedMember.sequenceNr()).isEmpty());
        });
        bucketisedMemberRepository.insertAll(members);
    }
}
