package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;

public interface LdesFragmentNamingStrategy {
    
	FragmentPair getFragmentationValue();
	
	default String generateFragmentName(LdesConfig config, FragmentInfo fragmentInfo) {
		StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(config.getHostName()).append("/").append(fragmentInfo.getCollectionName());
        
        if (!fragmentInfo.getFragmentPairs().isEmpty()){
            stringBuilder.append("?");
            fragmentInfo.getFragmentPairs().forEach(fragmentPair -> stringBuilder.append(fragmentPair.fragmentKey()).append("=").append(fragmentPair.fragmentValue()).toString());
        }
        
        return stringBuilder.toString();
    }
}
