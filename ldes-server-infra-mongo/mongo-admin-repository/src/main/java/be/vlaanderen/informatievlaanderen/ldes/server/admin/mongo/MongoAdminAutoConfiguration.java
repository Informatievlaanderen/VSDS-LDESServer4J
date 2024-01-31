package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.repository.DcatDatasetRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.repository.ShaclShapeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.DcatDatasetMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.repository.DcatDatasetEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.DcatServerMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.repository.DcatCatalogEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.EventStreamMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.ShaclShapeMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.repository.ShaclShapeEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.DcatViewMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.ViewMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.repository.DataServiceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.repository.ViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.service.DcatServiceEntityConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.service.ViewEntityConverter;
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
	public ViewRepository viewMongoRepository(final ViewEntityRepository viewEntityRepository,
											  final ViewEntityConverter viewEntityConverter) {
		return new ViewMongoRepository(viewEntityRepository, viewEntityConverter);
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
