package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.sequential.SequentialFragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimeBasedFragmentNamingStrategy;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.TimeBasedFragmentCreator.DATE_TIME_TYPE;
import static org.apache.jena.rdf.model.ResourceFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LdesConfig.class })
@EnableConfigurationProperties
@ActiveProfiles("test")
class TimeBasedFragmentCreatorTest {

	@Autowired
	private LdesConfig ldesConfig;

	private FragmentCreator fragmentCreator;
	private LdesMemberRepository ldesMemberRepository;
	private LdesFragmentRepository ldesFragmentRepository;

	@BeforeEach
	void setUp() {
		SequentialFragmentationConfig timeBasedConfig = createSequentialFragmentationConfig();
		LdesFragmentNamingStrategy timeBasedFragmentNamingStrategy = new TimeBasedFragmentNamingStrategy();
		ldesFragmentRepository = mock(LdesFragmentRepository.class);
		ldesMemberRepository = mock(LdesMemberRepository.class);
		fragmentCreator = new TimeBasedFragmentCreator(ldesConfig, timeBasedConfig, timeBasedFragmentNamingStrategy,
				ldesMemberRepository, ldesFragmentRepository);
	}

	@Test
	@DisplayName("Creating First Time-Based Fragment")
	void when_NoFragmentExists_thenNewFragmentIsCreated() {
		LdesMember ldesMember = createLdesMember();

		LdesFragment newFragment = fragmentCreator.createNewFragment(Optional.empty(), null);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		assertEquals(0, newFragment.getCurrentNumberOfMembers());
		assertEquals(0, newFragment.getRelations().size());
		verifyNoInteractions(ldesFragmentRepository);
	}

	@Test
	@DisplayName("Creating New Time-BasedFragment")
	void when_AFragmentAlreadyExists_thenNewFragmentIsCreatedAndRelationsAreUpdated() {
		LdesMember newLdesMember = createLdesMember();
		LdesMember ldesMemberOfFragment = createLdesMember();
		LdesFragment existingLdesFragment = new LdesFragment("someId",
				new FragmentInfo("viewShortName", List.of(new FragmentPair("Path", "Value"))));
		existingLdesFragment.addMember(ldesMemberOfFragment.getLdesMemberId());
		when(ldesMemberRepository.getLdesMemberById(ldesMemberOfFragment.getLdesMemberId()))
				.thenReturn(Optional.of(ldesMemberOfFragment));

		LdesFragment newFragment = fragmentCreator.createNewFragment(Optional.of(existingLdesFragment), null);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		assertEquals(0, newFragment.getCurrentNumberOfMembers());
		verifyRelationOfFragment(newFragment, "Path", "someId", "Value", "tree:LessThanOrEqualToRelation");
		verifyRelationOfFragment(existingLdesFragment, "generatedAtTime",
				"http://localhost:8080/mobility-hindrances?generatedAtTime=2020-12-28T09:36:37.127Z",
				"2020-12-28T09:36:37.127Z", "tree:GreaterThanOrEqualToRelation");
		verify(ldesFragmentRepository, times(1)).saveFragment(existingLdesFragment);
		// verify(ldesMemberRepository,
		// times(1)).getLdesMemberById(ldesMemberOfFragment.getLdesMemberId(MEMBER_TYPE));
	}

	@Test
	@DisplayName("Creating First Time-Based Fragment")
	void when_FragmentIsFull_NewFragmentNeedsToBeCreated() {
		LdesFragment ldesFragment = new LdesFragment("someId",
				new FragmentInfo("viewShortName", List.of(new FragmentPair("Path", "Value"))));
		ldesFragment.addMember("member1");
		assertFalse(fragmentCreator.needsToCreateNewFragment(ldesFragment));
		ldesFragment.addMember("member2");
		assertFalse(fragmentCreator.needsToCreateNewFragment(ldesFragment));
		ldesFragment.addMember("member3");
		assertTrue(fragmentCreator.needsToCreateNewFragment(ldesFragment));
	}

	private void verifyAssertionsOnAttributesOfFragment(LdesFragment ldesFragment) {
		assertEquals("http://localhost:8080/testData?generatedAtTime", ldesFragment.getFragmentId().split("=")[0]);
		assertEquals("testData", ldesFragment.getFragmentInfo().getCollectionName());
		assertEquals("generatedAtTime", ldesFragment.getFragmentInfo().getPath());
	}

	private LdesMember createLdesMember() {
		Model ldesMemberModel = ModelFactory.createDefaultModel();
		ldesMemberModel.add(createStatement(
				createResource("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483"),
				createProperty("http://www.w3.org/ns/prov#generatedAtTime"),
				createStringLiteral("2020-12-28T09:36:37.127Z")));
		ldesMemberModel.add(createStatement(
				createResource("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483"),
				createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
				createResource("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder")));
		ldesMemberModel.add(createStatement(createResource("http://localhost:8080/mobility-hindrances"), TREE_MEMBER,
				createResource(
						"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483")));
		return new LdesMember("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483",
				ldesMemberModel);
	}

	private void verifyRelationOfFragment(LdesFragment newFragment, String expectedTreePath, String expectedTreeNode,
			String expectedTreeValue, String expectedRelation) {
		assertEquals(1, newFragment.getRelations().size());
		TreeRelation actualTreeRelationOnNewFragment = newFragment.getRelations().get(0);
		TreeRelation expectedTreeRelationOnNewFragment = new TreeRelation(expectedTreePath, expectedTreeNode,
				expectedTreeValue, DATE_TIME_TYPE, expectedRelation);
		assertEquals(expectedTreeRelationOnNewFragment.getTreePath(), actualTreeRelationOnNewFragment.getTreePath());
		assertEquals(expectedTreeRelationOnNewFragment.getRelation(), actualTreeRelationOnNewFragment.getRelation());
	}

	private SequentialFragmentationConfig createSequentialFragmentationConfig() {
		SequentialFragmentationConfig config = new SequentialFragmentationConfig();
		config.setMemberLimit(3L);
		return config;
	}
}
