package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;

import java.util.List;

@Document("ldesfragment")
public class LdesFragmentEntity {
    @Id
    private final String id;

    private final FragmentInfo fragmentInfo;
    private final List<TreeRelation> relations;

    private final List<String> members;

    public LdesFragmentEntity(String id, FragmentInfo fragmentInfo, List<TreeRelation> relations,
            List<String> members) {
        this.id = id;
        this.fragmentInfo = fragmentInfo;
        this.relations = relations;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public FragmentInfo getFragmentInfo() {
        return fragmentInfo;
    }

    public Boolean isImmutable() {
        return fragmentInfo.getImmutable();
    }

    public List<String> getMembers() {
        return members;
    }

    public LdesFragment toLdesFragment() {
        LdesFragment ldesFragment = new LdesFragment(id, fragmentInfo);
        relations.forEach(ldesFragment::addRelation);
        members.forEach(ldesFragment::addMember);
        return ldesFragment;
    }

    public static LdesFragmentEntity fromLdesFragment(LdesFragment ldesFragment) {
        return new LdesFragmentEntity(ldesFragment.getFragmentId(), ldesFragment.getFragmentInfo(),
                ldesFragment.getRelations(),
                ldesFragment.getMemberIds());
    }
}
