package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.TreeRelation;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("ldesfragment")
public class LdesFragmentEntity {
    @Id
    private final String id;

    private final FragmentInfo fragmentInfo;
    private final List<TreeRelation> relations;

    @DBRef
    private final List<LdesMemberEntity> members;

    public LdesFragmentEntity(String id, FragmentInfo fragmentInfo, List<TreeRelation> relations,
            List<LdesMemberEntity> members) {
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

    public List<LdesMemberEntity> getMembers() {
        return members;
    }

    public LdesFragment toLdesFragment() {
        LdesFragment ldesFragment = new LdesFragment(id, fragmentInfo);
        relations.forEach(ldesFragment::addRelation);
        members.forEach(ldesMemberEntity -> ldesFragment.addMember(ldesMemberEntity.toLdesMember()));
        return ldesFragment;
    }

    public static LdesFragmentEntity fromLdesFragment(LdesFragment ldesFragment) {
        return new LdesFragmentEntity(ldesFragment.getFragmentId(), ldesFragment.getFragmentInfo(),
                ldesFragment.getRelations(),
                ldesFragment.getMembers().stream().map(LdesMemberEntity::fromLdesMember).toList());
    }
}
