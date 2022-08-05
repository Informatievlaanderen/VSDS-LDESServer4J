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

    private GeospatialBucketiser geospatialBucketiser;
    private final GeospatialConfig geospatialConfig = new GeospatialConfig();

    // GIVEN

    @Given("a configured geo-spatial bucketising property {string}")
    public void aConfiguredGeoSpatialBucketisingProperty(String propertyToBucketiseOn) {
        geospatialConfig.setBucketiserProperty(propertyToBucketiseOn);
    }

    @Given("a configured max zoom level at {int}")
    public void aConfiguredMaxZoomLevelAt(int maxZoomLevel) {
        geospatialConfig.setMaxZoomLevel(maxZoomLevel);
    }

    @Given("a configured member limit of {long} per fragment")
    public void aConfiguredMemberLimitOfPerFragment(long memberLimit) {
        geospatialConfig.setMemberLimit(memberLimit);
    }

    @Given("a geo-spatial bucketiser with the defined configuration")
    public void aGeoSpatialBucketiserWithTheDefinedConfiguration() {
        geospatialBucketiser = new GeospatialBucketiser(geospatialConfig);
    }


    @When("I bucketise a member {string}")
    public void iBucketiseAMember(String arg0) {
        
    }

    @Then("the bucketiser calculates a bucket based on {string}")
    public void theBucketiserCalculatesABucketBasedOn(String arg0) {
        
    }

    @When("a member contains a polygon spanning multiple tiles")
    public void aMemberContainsAPolygonSpanningMultipleTiles() {
        
    }

    @Then("the member buckets contain all these tiles.")
    public void theMemberBucketsContainAllTheseTiles() {
        
    }

    @Then("it does not store the buckets")
    public void itDoesNotStoreTheBuckets() {
    }


    @Then("I expect {int} fragments to be created")
    public void iExpectFragmentsToBeCreated(int arg0) {
        
    }

    @And("first member fragment has {int} members")
    public void firstMemberFragmentHasCountMembers(int memberCount) {
        
    }


    @And("the second member fragment has {int} members")
    public void theSecondMemberFragmentHasMemberResidueMembers(int memberCount) {
    }

    @Then("I expect {long} root fragments, {long} tile fragments and {long} memberFragments")
    public void iExpectRootCountRootFragmentsTileCountTileFragmentsAndMemberFragmentCountMemberFragments(long rootCount, long tileCount, long memberFragmentCount) {
        
    }

    @And("I expect the member to appear in all member fragments")
    public void iExpectTheMemberToAppearInAllMemberFragments() {
    }

    @And("the fragments contain {long} members")
    public void theFragmentsContainMaxMemberCountMembers(long memberCount) {
    }
}
