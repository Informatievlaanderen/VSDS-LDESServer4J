package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class GeospatialStepDefinitions {

    // WHEN

    @When("^I configure it to calculate buckets based on property A$")
    public void iConfigureItToCalculateBucketsBasedOnPropertyA() {
    }

    @When("^a member contains a polygon spanning multiple tiles$")
    public void aMemberContainsAPolygonSpanningMultipleTiles() {
    }

    @When("^it bucketizes$")
    public void itBucketizes() {
    }

    @And("^I bucketize a member containing geographic properties A and B$")
    public void iBucketizeAMemberContainingGeographicPropertiesAAndB() {
    }

    @Then("^the geo-spatial bucketizer calculates buckets based on property A$")
    public void theGeoSpatialBucketizerCalculatesBucketsBasedOnPropertyA() {
    }

    @Given("^a geo-spatial bucketizer$")
    public void aGeoSpatialBucketizer() {
    }

    @When("^I configure it to store (\\d+) members per fragment")
    public void iConfigureItToStoreCountMembersPerFragment(int memberLimit) {
    }

    @When("^i configure the max zoom level at (\\d+)")
    public void iConfigureTheMaxZoomLevelAtMaxZoomLevel(int maxZoomLevel) {
    }

    @When("^I bucketize a member at the boundingbox of zoom level (\\d+)")
    public void iBucketizeAMemberAtTheBoundingboxOfZoomLevelHigherMemberZoomLevel(int higherMemberZoomLevel) {
    }

    // THEN

    @Then("^the member buckets contain all these tiles\\.$")
    public void theMemberBucketsContainAllTheseTiles() {
    }

    @Then("^it does not store the buckets$")
    public void itDoesNotStoreTheBuckets() {
    }

    @Then("^I expect (\\d+) fragments to be created$")
    public void iExpectFragmentsToBeCreated(int fragmentsCount) {
    }

    @Then("^I expect following fragments \\((\\d+) root, (\\d+) tile, (\\d+) membersFragment\\)$")
    public void iExpectFollowingFragmentsRootTileMembersFragment(int arg0, int arg1, int arg2) {
    }

    @And("^the second member fragment has (\\d+) members$")
    public void theSecondMemberFragmentHasMemberResidueMembers(int memberCount) {
    }

    @And("^I pass it (\\d+) members belonging to the same tile")
    public void iPassItMemberAmountMembersBelongingToTheSameTile(int memberAmount) {
    }

    @And("^first member fragment has (\\d+) members")
    public void firstMemberFragmentHasCountMembers(int memberCount) {
    }

    @And("^the second member fragment has (\\d+) member")
    public void theSecondMemberFragmentHasMemberResidueMember(int memberResidue) {
    }
}
