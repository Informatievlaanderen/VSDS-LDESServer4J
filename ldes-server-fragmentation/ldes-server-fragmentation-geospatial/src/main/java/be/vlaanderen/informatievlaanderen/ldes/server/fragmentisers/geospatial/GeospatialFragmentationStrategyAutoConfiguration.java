package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import jakarta.annotation.PostConstruct;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.GeospatialFragmentationStrategy.GEOSPATIAL_FRAGMENTATION;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class GeospatialFragmentationStrategyAutoConfiguration {

	@SuppressWarnings("java:S6830")
	@Bean(GEOSPATIAL_FRAGMENTATION)
	public GeospatialFragmentationStrategyWrapper geospatialFragmentationStrategyWrapper() {
		return new GeospatialFragmentationStrategyWrapper();
	}

	/**
	 * Building the geospatial database can take some time. If we do not do this on
	 * initialization, the first
	 * ingestion will take a lot of time because of the database that needs to be
	 * constructed.
	 */
	@PostConstruct
	void triggerEspgDatabaseInitializationOnStartup() {
		String example = "<init-server> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/9.9.1/31370> POINT (0,0)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .";
		RDFParser.create().fromString(example).lang(Lang.NQUADS).build().toModel();
	}

}
