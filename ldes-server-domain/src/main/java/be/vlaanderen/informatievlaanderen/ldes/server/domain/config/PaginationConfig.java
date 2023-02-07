package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.PaginationExecutorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class PaginationConfig {

	@Bean
	@DependsOn({ "treeRelationsRepository" })
	public PaginationExecutorImpl paginationExecutor(@Autowired TreeRelationsRepository treeRelationsRepository) {
		return new PaginationExecutorImpl(treeRelationsRepository);
	}
}
