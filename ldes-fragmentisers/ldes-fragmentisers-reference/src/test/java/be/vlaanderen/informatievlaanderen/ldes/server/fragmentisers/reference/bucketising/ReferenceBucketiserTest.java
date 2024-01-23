package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.config.ReferenceConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReferenceBucketiserTest {

    private final ReferenceConfig config = new ReferenceConfig(RDF.type.getURI());
    private ReferenceBucketiser referenceBucketiser;

    private final String memberId = "parcels/https://data.vlaanderen.be/id/perceel/13374D0779-00D003/2022-11-29T11:37:27+01:00";

    @BeforeEach
    void setUp() {
        referenceBucketiser = new ReferenceBucketiser(config);
    }

    @Test
    void shouldReturnSetOfFoundResources() {
        Model model = RDFParser.source("member-with-two-types.ttl").toModel();

        assertThat(referenceBucketiser.bucketise(memberId, model))
                .hasSize(2)
                .contains("https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel")
                .contains("https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Gebouw");
    }

    @Test
    void shouldSkipResultsThatAreNotUris() {
        Model model = RDFParser.source("member-with-string-type.ttl").toModel();

        assertThat(referenceBucketiser.bucketise(memberId, model)).isEmpty();
    }

}