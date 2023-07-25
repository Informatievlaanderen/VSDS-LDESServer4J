package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MemberToFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.mapper.MemberToFragmentEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.AllocationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.FragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.MemberToFragmentEntityRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class MongoFragmentationAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public AllocationRepository allocationRepository(final AllocationEntityRepository repository) {
		return new AllocationMongoRepository(repository);
	}

	@Bean
	@ConditionalOnMissingBean
	public FragmentRepository fragmentRepository(final FragmentEntityRepository repository,
			MongoTemplate mongoTemplate) {
		return new FragmentMongoRepository(repository, mongoTemplate);
	}

	@Bean
	@ConditionalOnMissingBean
	public MemberToFragmentRepository membersToFragmentRepository(MemberToFragmentEntityRepository repository,
			MemberToFragmentEntityMapper memberToFragmentEntityMapper) {
		return new MemberToFragmentMongoRepository(repository, memberToFragmentEntityMapper);
	}

}
