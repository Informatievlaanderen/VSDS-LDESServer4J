package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesFragmentRespository;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TimeBasedFragmentCreatorTest {


    private FragmentCreator fragmentCreator;
    private LdesFragmentRespository ldesFragmentRespository;

    @BeforeEach
    void setUp() {
        LdesConfig ldesConfig = createLdesConfig();
        ViewConfig viewConfig = createViewConfig();
        ldesFragmentRespository = mock(LdesFragmentRespository.class);
        fragmentCreator = new TimeBasedFragmentCreator(ldesConfig, viewConfig, ldesFragmentRespository);
    }


    @Test
    @DisplayName("Creating First Time-Based Fragment")
    void when_NoFragmentExists_thenNewFragmentIsCreated() {
        LdesMember ldesMember = new LdesMember(ModelFactory.createDefaultModel());

        LdesFragment newFragment = fragmentCreator.createNewFragment(Optional.empty(), ldesMember);

        verifyAssertionsOnAttributesOfFragment(newFragment);
        assertEquals(0, newFragment.getCurrentNumberOfMembers());
        assertEquals(0, newFragment.getRelations().size());
        verifyNoInteractions(ldesFragmentRespository);
    }

    @Test
    @DisplayName("Creating New Time-BasedFragment")
    void when_AFragmentAlreadyExists_thenNewFragmentIsCreatedAndRelationsAreUpdated() {
        LdesMember ldesMember = new LdesMember(ModelFactory.createDefaultModel());
        LdesFragment existingLdesFragment = new LdesFragment("someId", new FragmentInfo("view", "shape", "viewShortName", "Path", "Value"));

        LdesFragment newFragment = fragmentCreator.createNewFragment(Optional.of(existingLdesFragment), ldesMember);

        verifyAssertionsOnAttributesOfFragment(newFragment);
        assertEquals(0, newFragment.getCurrentNumberOfMembers());
        verifyRelationOfFragment(newFragment, "Path",  "someId", "Value", "tree:LesserThanRelation");
        verifyRelationOfFragment(existingLdesFragment, "http://www.w3.org/ns/prov#generatedAtTime",  "http://localhost:8080/mobility-hindrances?generatedAtTime=http://www.w3.org/ns/prov#generatedAtTime","http://www.w3.org/ns/prov#generatedAtTime", "tree:GreaterThanRelation");
        verify(ldesFragmentRespository, times(1)).saveFragment(existingLdesFragment);
    }

    private void verifyAssertionsOnAttributesOfFragment(LdesFragment newFragment) {
        assertEquals("http://localhost:8080/mobility-hindrances?generatedAtTime=http://www.w3.org/ns/prov#generatedAtTime", newFragment.getFragmentId());
        assertEquals("http://localhost:8080/mobility-hindrances", newFragment.getFragmentInfo().getView());
        assertEquals("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape", newFragment.getFragmentInfo().getShape());
        assertEquals("mobility-hindrances", newFragment.getFragmentInfo().getViewShortName());
        assertEquals("http://www.w3.org/ns/prov#generatedAtTime", newFragment.getFragmentInfo().getPath());
        assertEquals("http://www.w3.org/ns/prov#generatedAtTime", newFragment.getFragmentInfo().getValue());
    }

    private void verifyRelationOfFragment(LdesFragment newFragment, String expectedTreePath, String expectedTreeNode, String expectedTreeValue, String expectedRelation) {
        assertEquals(1, newFragment.getRelations().size());
        TreeRelation actualTreeRelationOnNewFragment = newFragment.getRelations().get(0);
        TreeRelation expectedTreeRelationOnNewFragment = new TreeRelation(expectedTreePath, expectedTreeNode, expectedTreeValue, expectedRelation);
        assertEquals(expectedTreeRelationOnNewFragment,actualTreeRelationOnNewFragment);
    }

    private ViewConfig createViewConfig() {
        ViewConfig viewConfig = new ViewConfig();
        viewConfig.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
        viewConfig.setMemberLimit(3L);
        viewConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
        viewConfig.setVersionOfPath("http://purl.org/dc/terms/isVersionOf");
        return viewConfig;
    }

    private LdesConfig createLdesConfig() {
        LdesConfig ldesConfig = new LdesConfig();
        ldesConfig.setHostName("http://localhost:8080");
        ldesConfig.setCollectionName("mobility-hindrances");
        return ldesConfig;
    }
}