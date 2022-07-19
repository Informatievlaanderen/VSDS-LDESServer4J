package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.timebasedfragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfConstants.TREE_MEMBER;
import static org.apache.jena.rdf.model.ResourceFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TimeBasedFragmentCreatorTest {
    private final String MEMBER_TYPE = "https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder";
    private FragmentCreator fragmentCreator;
    private LdesFragmentRespository ldesFragmentRespository;
    private LdesMemberRepository ldesMemberRepository;

    @BeforeEach
    void setUp() {
        LdesConfig ldesConfig = createLdesConfig();
        TimeBasedConfig timeBasedConfig = createTimeBasedConfig();
        ldesFragmentRespository = mock(LdesFragmentRespository.class);
        ldesMemberRepository = mock(LdesMemberRepository.class);
        fragmentCreator = new TimeBasedFragmentCreator(ldesConfig, timeBasedConfig, ldesFragmentRespository, ldesMemberRepository);
    }

    @Test
    @DisplayName("Creating First Time-Based Fragment")
    void when_NoFragmentExists_thenNewFragmentIsCreated() {
        LdesMember ldesMember = createLdesMember();

        LdesFragment newFragment = fragmentCreator.createNewFragment(Optional.empty(), ldesMember);

        verifyAssertionsOnAttributesOfFragment(newFragment);
        assertEquals(0, newFragment.getCurrentNumberOfMembers());
        assertEquals(0, newFragment.getRelations().size());
        verifyNoInteractions(ldesFragmentRespository);
    }

    @Test
    @DisplayName("Creating New Time-BasedFragment")
    void when_AFragmentAlreadyExists_thenNewFragmentIsCreatedAndRelationsAreUpdated() {
        LdesMember newLdesMember = createLdesMember();
        LdesMember ldesMemberOfFragment = createLdesMember();
        LdesFragment existingLdesFragment = new LdesFragment("someId", new FragmentInfo("view", "shape", "viewShortName", List.of(new FragmentPair("Path", "Value"))));
        existingLdesFragment.addMember(ldesMemberOfFragment.getLdesMemberId(MEMBER_TYPE));
        when(ldesMemberRepository.getLdesMemberById(ldesMemberOfFragment.getLdesMemberId(MEMBER_TYPE))).thenReturn(Optional.of(ldesMemberOfFragment));

        LdesFragment newFragment = fragmentCreator.createNewFragment(Optional.of(existingLdesFragment), newLdesMember);

        verifyAssertionsOnAttributesOfFragment(newFragment);
        assertEquals(0, newFragment.getCurrentNumberOfMembers());
        verifyRelationOfFragment(newFragment, "Path", "someId", "Value", "tree:LessThanOrEqualToRelation");
        verifyRelationOfFragment(existingLdesFragment, "generatedAtTime", "http://localhost:8080/mobility-hindrances?generatedAtTime=2020-12-28T09:36:37.127Z", "2020-12-28T09:36:37.127Z", "tree:GreaterThanOrEqualToRelation");
        verify(ldesFragmentRespository, times(1)).saveFragment(existingLdesFragment);
        verify(ldesMemberRepository, times(1)).getLdesMemberById(ldesMemberOfFragment.getLdesMemberId(MEMBER_TYPE));
    }

    @Test
    @DisplayName("Creating First Time-Based Fragment")
    void when_FragmentIsFull_NewFragmentNeedsToBeCreated() {
        LdesFragment ldesFragment = new LdesFragment("someId", new FragmentInfo("view", "shape", "viewShortName", List.of(new FragmentPair("Path", "Value"))));
        ldesFragment.addMember("member1");
        assertFalse(fragmentCreator.needsToCreateNewFragment(ldesFragment));
        ldesFragment.addMember("member2");
        assertFalse(fragmentCreator.needsToCreateNewFragment(ldesFragment));
        ldesFragment.addMember("member3");
        assertTrue(fragmentCreator.needsToCreateNewFragment(ldesFragment));

    }

    private void verifyAssertionsOnAttributesOfFragment(LdesFragment ldesFragment) {
        assertEquals("http://localhost:8080/mobility-hindrances?generatedAtTime", ldesFragment.getFragmentId().split("=")[0]);
        assertEquals("http://localhost:8080/mobility-hindrances", ldesFragment.getFragmentInfo().getView());
        assertEquals("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape", ldesFragment.getFragmentInfo().getShape());
        assertEquals("mobility-hindrances", ldesFragment.getFragmentInfo().getCollectionName());
        assertEquals("generatedAtTime", ldesFragment.getFragmentInfo().getPath());
    }

    private LdesMember createLdesMember() {
        Model ldesMemberModel = ModelFactory.createDefaultModel();
        ldesMemberModel.add(createStatement(createResource("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483"), createProperty("http://www.w3.org/ns/prov#generatedAtTime"), createStringLiteral("2020-12-28T09:36:37.127Z")));
        ldesMemberModel.add(createStatement(createResource("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483"), createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), createResource("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder")));
        ldesMemberModel.add(createStatement(createResource("http://localhost:8080/mobility-hindrances"), TREE_MEMBER, createResource("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483")));
        return new LdesMember(ldesMemberModel);
    }

    private void verifyRelationOfFragment(LdesFragment newFragment, String expectedTreePath, String expectedTreeNode, String expectedTreeValue, String expectedRelation) {
        assertEquals(1, newFragment.getRelations().size());
        TreeRelation actualTreeRelationOnNewFragment = newFragment.getRelations().get(0);
        TreeRelation expectedTreeRelationOnNewFragment = new TreeRelation(expectedTreePath, expectedTreeNode, expectedTreeValue, expectedRelation);
        assertEquals(expectedTreeRelationOnNewFragment.getTreePath(), actualTreeRelationOnNewFragment.getTreePath());
        assertEquals(expectedTreeRelationOnNewFragment.getRelation(), actualTreeRelationOnNewFragment.getRelation());
    }

    private TimeBasedConfig createTimeBasedConfig() {
        TimeBasedConfig timeBasedConfig = new TimeBasedConfig();
        timeBasedConfig.setMemberLimit(3L);
        return timeBasedConfig;
    }

    private LdesConfig createLdesConfig() {
        LdesConfig ldesConfig = new LdesConfig();
        ldesConfig.setHostName("http://localhost:8080");
        ldesConfig.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
        ldesConfig.setCollectionName("mobility-hindrances");
        return ldesConfig;
    }
}