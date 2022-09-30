package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedFragmentationConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedFragmentCreator.DATE_TIME_TYPE;
import static org.apache.jena.rdf.model.ResourceFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimeBasedFragmentCreatorTest {

	private static final String VIEW = "view";
	private TimeBasedFragmentCreator fragmentCreator;
	private LdesMemberRepository ldesMemberRepository;
	private LdesFragmentRepository ldesFragmentRepository;

	@BeforeEach
	void setUp() {
		TimebasedFragmentationConfig timeBasedConfig = createSequentialFragmentationConfig();
		ldesFragmentRepository = mock(LdesFragmentRepository.class);
		ldesMemberRepository = mock(LdesMemberRepository.class);
		fragmentCreator = new TimeBasedFragmentCreator(timeBasedConfig,
				ldesFragmentRepository);
	}

	@Test
	@DisplayName("Creating First Time-Based Fragment")
	void when_NoFragmentExists_thenNewFragmentIsCreated() {
		FragmentInfo parentFragmentInfo = new FragmentInfo(VIEW, List.of());
		LdesFragment newFragment = fragmentCreator.createNewFragment(Optional.empty(), parentFragmentInfo);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		assertEquals(0, newFragment.getCurrentNumberOfMembers());
		assertEquals(0, newFragment.getRelations().size());
		verifyNoInteractions(ldesFragmentRepository);
	}

	@Test
	@DisplayName("Creating New Time-BasedFragment")
	void when_AFragmentAlreadyExists_thenNewFragmentIsCreatedAndRelationsAreUpdated() {
		FragmentInfo parentFragmentInfo = new FragmentInfo(VIEW, List.of());

		LdesMember ldesMemberOfFragment = createLdesMember();
		LdesFragment existingLdesFragment = new LdesFragment(
				new FragmentInfo(VIEW, List.of(new FragmentPair(GENERATED_AT_TIME,
						"2020-12-28T09:36:37.127Z"))));
		existingLdesFragment.addMember(ldesMemberOfFragment.getLdesMemberId());
		when(ldesMemberRepository.getLdesMemberById(ldesMemberOfFragment.getLdesMemberId()))
				.thenReturn(Optional.of(ldesMemberOfFragment));

		LdesFragment newFragment = fragmentCreator.createNewFragment(Optional.of(existingLdesFragment),
				parentFragmentInfo);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		assertEquals(0, newFragment.getCurrentNumberOfMembers());
		verifyRelationOfFragment(newFragment, PROV_GENERATED_AT_TIME, "someId", "Value",
				TREE_LESSER_THAN_OR_EQUAL_TO_RELATION);
		verifyRelationOfFragment(existingLdesFragment, PROV_GENERATED_AT_TIME,
				"http://localhost:8080/mobility-hindrances?generatedAtTime=2020-12-28T09:36:37.127Z",
				"2020-12-28T09:36:37.127Z", TREE_GREATER_THAN_OR_EQUAL_TO_RELATION);
		verify(ldesFragmentRepository, times(1)).saveFragment(existingLdesFragment);
	}

	@Test
	@DisplayName("Creating New Time-Based Fragment")
	void when_FragmentIsFull_NewFragmentNeedsToBeCreated() {
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo(VIEW, List.of(new FragmentPair("Path",
						"Value"))));
		ldesFragment.addMember("member1");
		assertFalse(fragmentCreator.needsToCreateNewFragment(ldesFragment));
		ldesFragment.addMember("member2");
		assertFalse(fragmentCreator.needsToCreateNewFragment(ldesFragment));
		ldesFragment.addMember("member3");
		assertTrue(fragmentCreator.needsToCreateNewFragment(ldesFragment));
	}

	private void verifyAssertionsOnAttributesOfFragment(LdesFragment ldesFragment) {
		assertEquals("/view?generatedAtTime",
				ldesFragment.getFragmentId().split("=")[0]);
		assertEquals(VIEW, ldesFragment.getFragmentInfo().getViewName());
		assertTrue(ldesFragment.getFragmentInfo().getValueOfKey(GENERATED_AT_TIME).isPresent());
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
		ldesMemberModel.add(createStatement(createResource("http://localhost:8080/mobility-hindrances"),
				TREE_MEMBER,
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
		assertEquals(expectedTreeRelationOnNewFragment.getTreePath(),
				actualTreeRelationOnNewFragment.getTreePath());
		assertEquals(expectedTreeRelationOnNewFragment.getRelation(),
				actualTreeRelationOnNewFragment.getRelation());
	}

	private TimebasedFragmentationConfig createSequentialFragmentationConfig() {
		TimebasedFragmentationConfig config = new TimebasedFragmentationConfig();
		config.setMemberLimit(3L);
		return config;
	}
}
