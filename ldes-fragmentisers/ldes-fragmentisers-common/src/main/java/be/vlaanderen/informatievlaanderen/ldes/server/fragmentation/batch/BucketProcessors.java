package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BucketProcessors {
	@Bean
	@StepScope
	public ItemProcessor<FragmentationMember, List<BucketisedMember>> viewBucketProcessor(
			FragmentationStrategyCollection fragmentationStrategyCollection,
			@Value("#{jobParameters['collectionName']}") String collectionName,
			@Value("#{jobParameters['viewName']}") String viewName
	) {
		final String composedViewName = new ViewName(collectionName, viewName).asString();
		return item -> fragmentationStrategyCollection.getFragmentationStrategyExecutor(composedViewName)
				.map(executor -> executor.bucketise(item))
				.orElse(null);
	}
}
