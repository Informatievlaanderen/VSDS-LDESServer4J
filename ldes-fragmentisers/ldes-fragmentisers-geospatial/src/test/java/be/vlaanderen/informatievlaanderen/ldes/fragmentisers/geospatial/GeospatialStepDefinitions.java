package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.config.GeospatialConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

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
        geospatialConfig.setZoomLevel(maxZoomLevel);
    }

    @Given("a geo-spatial bucketiser with the defined configuration")
    public void aGeoSpatialBucketiserWithTheDefinedConfiguration() {
        geospatialBucketiser = new GeospatialBucketiser(geospatialConfig, null, null);
    }


    @When("I bucketise a member {string}")
    public void iBucketiseAMember(String ldesMemberPath) {
        LdesMember ldesMember = new LdesMember("id_1", RdfModelConverter.fromString(getLdesMemberString(ldesMemberPath), Lang.NQUADS));
        geospatialBucketiser.bucketise(ldesMember);
    }

    @Then("the bucketiser calculates a bucket based on {string}")
    public void theBucketiserCalculatesABucketBasedOn(String tile) {

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

    private static String getLdesMemberString(String filePath) {
        try {
            return FileUtils.readFileToString(ResourceUtils.getFile("classpath:"+filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
