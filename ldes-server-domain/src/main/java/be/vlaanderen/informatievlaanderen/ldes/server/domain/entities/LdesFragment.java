package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.FragmentIdConverter.toFragmentId;

public class LdesFragment {

    private final String fragmentId;

    private final FragmentInfo fragmentInfo;

    private final List<String> memberIds;

    private final List<TreeRelation> relations;

    public LdesFragment(String fragmentId, FragmentInfo fragmentInfo) {
        this.fragmentId = fragmentId;
        this.fragmentInfo = fragmentInfo;
        this.relations = new ArrayList<>();
        this.memberIds = new ArrayList<>();
    }

    public static LdesFragment newFragment(String hostname, FragmentInfo fragmentInfo) {
        String fragmentId = toFragmentId(hostname, fragmentInfo.getCollectionName(), fragmentInfo.getPath(),
                fragmentInfo.getValue());
        return new LdesFragment(fragmentId, fragmentInfo);
    }

    public void addRelation(TreeRelation treeRelation) {
        this.relations.add(treeRelation);
    }

    public String getFragmentId() {
        return fragmentId;
    }

    public FragmentInfo getFragmentInfo() {
        return fragmentInfo;
    }

    public List<TreeRelation> getRelations() {
        return relations;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void addMember(String ldesMemberId) {
        memberIds.add(ldesMemberId);
    }

    public int getCurrentNumberOfMembers() {
        return memberIds.size();
    }

    public void setImmutable(boolean immutable) {
        this.fragmentInfo.setImmutable(immutable);
    }

    public boolean isImmutable() {
        return this.fragmentInfo.getImmutable();
    }

    public boolean isExistingFragment() {
        return this.fragmentInfo.getValue()!=null;
    }
}
