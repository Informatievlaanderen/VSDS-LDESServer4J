package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.DcatDataServicePostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.repository.DataServiceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.DcatDatasetPostgresRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.repository.DcatDatasetEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.DcatCatalogPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.repository.DcatCatalogEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.EventStreamPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.ShaclShapePostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.repository.ShaclShapeEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.ViewPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.repository.ViewEntityRepository;
import org.apache.jena.riot.Lang;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class PostgresAdminAutoConfiguration {
	public static final Lang SERIALISATION_LANG = Lang.TURTLE;

	@Bean
	@ConditionalOnMissingBean
	public EventStreamRepository eventStreamRepository(final EventStreamEntityRepository eventStreamEntityRepository) {
		return new EventStreamPostgresRepository(eventStreamEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public ShaclShapeRepository shaclShapePostgresRepository(final ShaclShapeEntityRepository shaclShapeEntityRepository) {
		return new ShaclShapePostgresRepository(shaclShapeEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public ViewRepository viewPostgresRepository(final ViewEntityRepository viewEntityRepository) {
		return new ViewPostgresRepository(viewEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public DcatDatasetRepository dcatDatasetPostgresRepository(
			final DcatDatasetEntityRepository dcatDatasetEntityRepository) {
		return new DcatDatasetPostgresRespository(dcatDatasetEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public DcatServerRepository serverDcatRepository(final DcatCatalogEntityRepository dcatCatalogEntityRepository) {
		return new DcatCatalogPostgresRepository(dcatCatalogEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public DcatViewRepository dcatViewPostgresRepository(final DataServiceEntityRepository dataServiceEntityRepository) {
		return new DcatDataServicePostgresRepository(dataServiceEntityRepository);
	}

}
