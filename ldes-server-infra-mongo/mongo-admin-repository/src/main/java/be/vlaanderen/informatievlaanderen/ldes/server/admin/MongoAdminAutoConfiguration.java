package be.vlaanderen.informatievlaanderen.ldes.server.admin;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.dcatdataset.DcatDatasetMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.dcatdataset.repository.DcatDatasetEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.dcatserver.DcatServerMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.dcatserver.repository.DcatCatalogEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.eventstream.EventStreamMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.shaclshape.ShaclShapeMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.shaclshape.repository.ShaclShapeEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.view.DcatViewMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.view.ViewMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.view.repository.DataServiceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.view.repository.ViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.view.service.DcatServiceEntityConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class MongoAdminAutoConfiguration {


	@Bean
	@ConditionalOnMissingBean
	public EventStreamRepository eventStreamRepository(final EventStreamEntityRepository eventStreamEntityRepository) {
		return new EventStreamMongoRepository(eventStreamEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public ShaclShapeRepository shaclShapeMongoRepository(final ShaclShapeEntityRepository shaclShapeEntityRepository) {
		return new ShaclShapeMongoRepository(shaclShapeEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public ViewRepository viewMongoRepository(final ViewEntityRepository viewEntityRepository) {
		return new ViewMongoRepository(viewEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public DcatDatasetRepository dcatDatasetMongoRepository(
			final DcatDatasetEntityRepository dcatDatasetEntityRepository) {
		return new DcatDatasetMongoRepository(dcatDatasetEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public DcatServerRepository serverDcatRepository(final DcatCatalogEntityRepository dcatCatalogEntityRepository) {
		return new DcatServerMongoRepository(dcatCatalogEntityRepository);
	}

	@Bean
	@ConditionalOnMissingBean
	public DcatViewRepository dcatViewMongoRepository(final DataServiceEntityRepository dataServiceEntityRepository) {
		return new DcatViewMongoRepository(dataServiceEntityRepository, new DcatServiceEntityConverter());
	}

}
