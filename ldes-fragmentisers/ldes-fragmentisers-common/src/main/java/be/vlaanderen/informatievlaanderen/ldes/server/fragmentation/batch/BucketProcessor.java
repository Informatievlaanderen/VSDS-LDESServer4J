package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@StepScope
public class BucketProcessor implements ItemProcessor<FragmentationMember, List<BucketisedMember>> {
	private final FragmentationStrategyCollection fragmentationCollections;
	private final Map<String, Object> jobParameters;

	public BucketProcessor(FragmentationStrategyCollection fragmentationCollections,
	                       @Value("#{jobParameters}") Map<String, Object> jobParameters) {
		this.fragmentationCollections = fragmentationCollections;
		this.jobParameters = jobParameters;
	}

	@Override
	public List<BucketisedMember> process(@NotNull FragmentationMember item) {
		if (jobParameters.containsKey("viewName")) {
			return fragmentationCollections.getFragmentationStrategyExecutor((String) jobParameters.get("viewName"))
					.map(fragmentationStrategyBatchExecutor -> fragmentationStrategyBatchExecutor.bucketise(item))
					.orElse(null);
		} else {
			return fragmentationCollections.getAllFragmentationStrategyExecutors(item.getCollectionName())
					.parallelStream()
					.map(fragmentationStrategyBatchExecutor -> fragmentationStrategyBatchExecutor.bucketise(item))
					.flatMap(List::stream)
					.toList();
		}
	}
}
