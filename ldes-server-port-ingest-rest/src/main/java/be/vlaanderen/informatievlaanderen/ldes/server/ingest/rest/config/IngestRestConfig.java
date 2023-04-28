package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.config;

import jakarta.annotation.PostConstruct;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IngestRestConfig {

	/**
	 * Building the geospatial database can take some time. If we do not do this on
	 * initialization, the first
	 * ingestion will take a lot of time because of the database that needs to be
	 * constructed.
	 */
	@PostConstruct
	void triggerEspgDatabaseInitializationOnStartup() {
		String example = "<init-server> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/9.9.1/31370> POINT (0,0)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .";
		RDFParser.fromString(example).lang(Lang.NQUADS).build().toModel();
	}

}
