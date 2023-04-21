package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.List;
import java.util.stream.Collectors;

public class LdesFragmentIdentifier {

    private final ViewName viewName;
    private final List<FragmentPair> fragmentPairs;

    public LdesFragmentIdentifier(ViewName viewName, List<FragmentPair> fragmentPairs) {
        this.viewName = viewName;
        this.fragmentPairs = fragmentPairs;
    }

    public static LdesFragmentIdentifier fromFragmentId(String fragmentId){
        // TODO PJ
        fragmentId.substring(1).split()
        this()
    }

    public String getFragmentId() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/").append(viewName.asString());

        if (!fragmentPairs.isEmpty()) {
            stringBuilder.append("?");
            stringBuilder.append(fragmentPairs.stream()
                    .map(fragmentPair -> fragmentPair.fragmentKey() + "=" + fragmentPair.fragmentValue())
                    .collect(Collectors.joining("&")));
        }

        return stringBuilder.toString();
    }
}
