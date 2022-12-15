package be.vlaanderen.informatievlaanderen.ldes.server.domain.caching;

import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

@Component
public class EtagCachingStrategy implements CachingStrategy {

	private final LdesConfig ldesConfig;

	public EtagCachingStrategy(final LdesConfig ldesConfig) {
		this.ldesConfig = ldesConfig;
	}

	@Override
	public String generateCacheIdentifier(EventStream eventStream) {
		return DigestUtils.sha256Hex(ldesConfig.getHostName() + "/" + eventStream.collection());
	}

	@Override
	public String generateCacheIdentifier(LdesFragment ldesFragment) {
		return DigestUtils.sha256Hex(
				ldesFragment.getFragmentId()
						+ ldesFragment.getRelations().stream()
								.map(relation -> relation.treeNode())
								.collect(Collectors.joining(""))
						+ ldesFragment.getMemberIds().stream()
								.collect(Collectors.joining("")));
	}
}
