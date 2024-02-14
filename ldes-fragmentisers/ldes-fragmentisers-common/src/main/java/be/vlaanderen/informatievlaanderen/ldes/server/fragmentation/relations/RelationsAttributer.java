package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

public interface RelationsAttributer {
    default TreeRelation getDefaultRelation(Fragment childFragment) {
        return new TreeRelation("", childFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION);
    }
}
