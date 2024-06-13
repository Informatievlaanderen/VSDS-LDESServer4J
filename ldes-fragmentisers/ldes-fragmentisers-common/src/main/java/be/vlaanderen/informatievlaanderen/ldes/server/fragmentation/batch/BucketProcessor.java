package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper.MemberMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper.MemberMapperCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@StepScope
public class BucketProcessor implements ItemProcessor<IngestedMember, List<BucketisedMember>> {
	private final FragmentationStrategyCollection fragmentationCollections;
	private final MemberMapperCollection memberMappers;
	private final Map<String, Object> jobParameters;

	public BucketProcessor(FragmentationStrategyCollection fragmentationCollections, MemberMapperCollection memberMappers,
	                       @Value("#{jobParameters}") Map<String, Object> jobParameters) {
		this.fragmentationCollections = fragmentationCollections;
		this.memberMappers = memberMappers;
		this.jobParameters = jobParameters;
	}

	@Override
	public List<BucketisedMember> process(IngestedMember item) {
		final MemberMapper memberMapper = memberMappers.getMemberMapper(item.getCollectionName())
				.orElseThrow(() -> new MissingResourceException("eventstream", item.getCollectionName()));

		if (jobParameters.containsKey("viewName")) {
			return fragmentationCollections.getFragmentationStrategyExecutor((String) jobParameters.get("viewName"))
					.map(fragmentationStrategyBatchExecutor -> fragmentationStrategyBatchExecutor.bucketise(memberMapper.mapToFragmentationMember(item)))
					.orElse(null);
		} else {
			return fragmentationCollections.getFragmentationStrategyExecutors(item.getCollectionName())
					.parallelStream()
					.map(fragmentationStrategyBatchExecutor -> fragmentationStrategyBatchExecutor.bucketise(memberMapper.mapToFragmentationMember(item)))
					.flatMap(List::stream)
					.toList();
		}
	}
}
