package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;// package

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
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

	private final GeospatialConfig geospatialConfig = new GeospatialConfig(".*",
			"http://www.opengis.net/ont/geosparql#asWKT",
			15, null);

	@Test
	@DisplayName("Bucketising of LdesMember with Lambert72 conversion")
	void when_MemberIsBucketized_CorrectBucketsAreReturned() throws URISyntaxException, IOException {
		bucketiser = new GeospatialBucketiser(geospatialConfig, new Lambert72CoordinateConverter());

		Set<String> expectedBuckets = Set.of("15/16743/11009", "15/16744/11009",
				"15/16743/11010", "15/16742/11010");
		LdesMember ldesMember = readLdesMemberFromFile(getClass().getClassLoader(),
				"examples/ldes-member-bucketising.nq");

		Set<String> actualBuckets = bucketiser.bucketise(ldesMember);

		assertEquals(4, actualBuckets.size());
		assertEquals(expectedBuckets, actualBuckets);
	}

	@Test
	@DisplayName("Bucketising of LdesMember with 2 geo properties")
	void when_MemberWith2GeoPropertiesIsBucketized_CorrectBucketsAreReturned() throws URISyntaxException, IOException {
		bucketiser = new GeospatialBucketiser(geospatialConfig, new NoopCoordinateConverter());

		Set<String> expectedBuckets = Set.of("15/16884/10974", "15/16882/10975");
		LdesMember ldesMember = readLdesMemberFromFile(getClass().getClassLoader(),
				"examples/ldes-member-2-geo-props-bucketising.nq");

		Set<String> actualBuckets = bucketiser.bucketise(ldesMember);

		assertEquals(2, actualBuckets.size());
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