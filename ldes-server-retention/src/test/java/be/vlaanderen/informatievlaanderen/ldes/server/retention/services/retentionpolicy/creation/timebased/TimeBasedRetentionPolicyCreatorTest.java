package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeBasedRetentionPolicyCreatorTest {

    private final TimeBasedRetentionPolicyCreator timeBasedRetentionPolicyCreator = new TimeBasedRetentionPolicyCreator();

    @Test
    void when_ModelDescribesAValidTimeBasedRetentionPolicy_then_ATimeBasedRetentionPolicyIsReturned()
            throws URISyntaxException {
        Model retentionModel = readModelFromFile("retentionpolicy/timebased/valid_timebased.ttl");

        RetentionPolicy retentionPolicy = timeBasedRetentionPolicyCreator.createRetentionPolicy(retentionModel);

        assertThat(retentionPolicy).isInstanceOf(TimeBasedRetentionPolicy.class);
    }

    @Test
    void when_ModelDoesNotExactlyHaveOneTreeValueStatement_then_AnIllegalArgumentExceptionIsThrown()
            throws URISyntaxException {
        Model retentionModel = readModelFromFile("retentionpolicy/timebased/invalid_timebased.ttl");

        assertThatThrownBy(() -> timeBasedRetentionPolicyCreator.createRetentionPolicy(retentionModel))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(getExpectedMessage());
    }

    private Model readModelFromFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
        return RDFDataMgr.loadModel(uri);
    }

    private String getExpectedMessage() {
        return """
                Cannot Create Time Based Retention Policy in which there is not exactly 1 https://w3id.org/tree#value statement.
                 Found 2 statements in :
                [ a                              <https://w3id.org/ldes#DurationAgoPolicy>;
                  <https://w3id.org/tree#value>  "PT3M"^^<http://www.w3.org/2001/XMLSchema#duration> , "PT2M"^^<http://www.w3.org/2001/XMLSchema#duration>
                ] .
                """;
    }
}