package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.BucketisedMemberSaver;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FragmentationConfigCompaction {

	@Bean("compactionFragmentation")
	public FragmentationStrategy compactionFragmentationStrategy(BucketisedMemberSaver bucketisedMemberSaver) {
		return new FragmentationStrategyImpl(bucketisedMemberSaver);
	}
}
