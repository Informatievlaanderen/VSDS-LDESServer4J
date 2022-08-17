package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LdesFragmentEntityTest {

    private static final String SHAPE = "https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape";
    private static final String HOSTNAME = "http://localhost:8080";
    private static final String COLLECTION_NAME = "mobility-hindrances";
    private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
    private static final String FRAGMENT_ID = "http://localhost:8080/mobility-hindrances?generatedAtTime=" + FRAGMENTATION_VALUE_1;
    private static final String TIMESTAMP_PATH = "http://www.w3.org/ns/prov#generatedAtTime";


    @DisplayName("Verify conversion from LdesFragment to LdesFragmentEntity")
    @Test
    void when_LdesFragmentIsConvertedToLdesFragmentEntity_FragmentInfoAndMembersAndMutabilityAreTheSame(){
        FragmentInfo fragmentInfo = new FragmentInfo(COLLECTION_NAME, List.of(new FragmentPair(TIMESTAMP_PATH, FRAGMENTATION_VALUE_1)));
        LdesFragment ldesFragment = new LdesFragment(FRAGMENT_ID, fragmentInfo);
        ldesFragment.addMember("member1");
        ldesFragment.addMember("member2");

        LdesFragmentEntity ldesFragmentEntity = LdesFragmentEntity.fromLdesFragment(ldesFragment);

        assertEquals(fragmentInfo,ldesFragmentEntity.getFragmentInfo());
        assertEquals(List.of("member1", "member2"),ldesFragmentEntity.getMembers());
        assertEquals(false, ldesFragmentEntity.isImmutable());
    }

}