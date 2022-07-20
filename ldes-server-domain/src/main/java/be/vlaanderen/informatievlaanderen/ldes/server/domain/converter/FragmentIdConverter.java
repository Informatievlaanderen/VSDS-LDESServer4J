package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;

import java.util.List;

public class FragmentIdConverter {
    private FragmentIdConverter() {
    }

    public static String toFragmentId(String hostLocation, String view, List<FragmentPair> fragmentPairs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(hostLocation).append("/").append(view);
        if(!fragmentPairs.isEmpty()){
            stringBuilder.append("?");
            fragmentPairs.forEach(fragmentPair -> stringBuilder.append(fragmentPair.fragmentKey()).append("=").append(fragmentPair.fragmentValue()).toString());
        }
        return stringBuilder.toString();
    }
}
