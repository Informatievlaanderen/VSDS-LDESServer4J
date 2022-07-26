package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.entities.LdesFragmentView;

public class LdesFragmentViewConverter {
    public LdesFragmentView convertToLdesFragmentView(LdesFragment ldesFragment){
        return new LdesFragmentView("","", true);
    }
}
