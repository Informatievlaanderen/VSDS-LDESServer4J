package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;// package

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeospatialBucketiserTest {

	private GeospatialBucketiser bucketiser;

	@BeforeEach
	void setUp() {
		GeospatialConfig geospatialConfig = new GeospatialConfig();
		geospatialConfig.setBucketiserProperty("http://www.opengis.net/ont/geosparql#asWKT");
		geospatialConfig.setMaxZoomLevel(15);
		bucketiser = new GeospatialBucketiser(geospatialConfig, new Lambert72CoordinateConverter());
	}

	@Test
	@DisplayName("Bucketising of LdesMember")
	void when_MemberIsBucketized_CorrectBucketsAreReturned() throws URISyntaxException, IOException {
		Set<String> expectedBuckets = Set.of("15/16743/11009", "15/16744/11009",
				"15/16743/11010", "15/16742/11010");
		LdesMember ldesMember = readLdesMemberFromFile(getClass().getClassLoader(),
				"examples/ldes-member-bucketising.nq");

		Set<String> actualBuckets = bucketiser.bucketise(ldesMember);

		assertEquals(4, actualBuckets.size());
		assertEquals(expectedBuckets, actualBuckets);
	}

	private LdesMember readLdesMemberFromFile(ClassLoader classLoader, String fileName)
			throws URISyntaxException, IOException {
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());

		Model outputModel = RDFParserBuilder.create()
				.fromString(Files.lines(Paths.get(file.toURI())).collect(Collectors.joining())).lang(Lang.NQUADS)
				.toModel();

		return new LdesMember("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				outputModel);
	}

}