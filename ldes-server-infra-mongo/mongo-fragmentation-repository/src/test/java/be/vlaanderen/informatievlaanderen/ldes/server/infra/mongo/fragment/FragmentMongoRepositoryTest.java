package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.FragmentMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.FragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.FragmentEntityRepository;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FragmentMongoRepositoryTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final String FIRST_VALUE = "2020-12-28T09:36:37.127Z";
	private static final String SECOND_VALUE = "2020-12-29T09:36:37.127Z";
	private static final String THIRD_VALUE = "2020-12-30T09:36:37.127Z";

	private final FragmentEntityRepository ldesFragmentEntityRepository = mock(FragmentEntityRepository.class);
	private final MongoTemplate mongoTemplate = mock(MongoTemplate.class);

	private final FragmentMongoRepository ldesFragmentMongoRepository = new FragmentMongoRepository(
			ldesFragmentEntityRepository, mongoTemplate);

	@ParameterizedTest
	@ArgumentsSource(LdesFragmentEntityListProvider.class)
	void when_RetrieveOpenFragment_FirstFragmentThatIsOpenAndBelongsToCollectionIsReturned(
			List<FragmentEntity> entitiesInRepository, String expectedFragmentId) {
		when(ldesFragmentEntityRepository
				.findAllByImmutableAndViewName(false,
						VIEW_NAME.asString()))
				.thenReturn(entitiesInRepository.stream()
						.filter(ldesFragmentEntity -> !ldesFragmentEntity.isImmutable())
						.filter(ldesFragmentEntity -> ldesFragmentEntity.getViewName()
								.equals(VIEW_NAME.asString()))
						.collect(Collectors.toList()));

		Optional<LdesFragment> ldesFragment = ldesFragmentMongoRepository.retrieveMutableFragment(
				VIEW_NAME.asString(),
				List.of());

		assertTrue(ldesFragment.isPresent());
		assertEquals(expectedFragmentId, ldesFragment.get().getFragmentIdString());
	}

	static class LdesFragmentEntityListProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(allMutable(), allMutable().get(0).getId()),
					Arguments.of(firstImmutable(), firstImmutable().get(1).getId()),
					Arguments.of(firstImmutableSecondOtherView(),
							firstImmutableSecondOtherView().get(2).getId()));
		}

		protected static List<FragmentEntity> allMutable() {
			return List.of(
					createLdesFragmentEntity(false, VIEW_NAME, FIRST_VALUE),
					createLdesFragmentEntity(false, VIEW_NAME, SECOND_VALUE),
					createLdesFragmentEntity(false, VIEW_NAME, THIRD_VALUE));
		}

		private static FragmentEntity createLdesFragmentEntity(boolean immutable, ViewName viewName, String value) {
			LdesFragment ldesFragment = new LdesFragment(new LdesFragmentIdentifier(viewName,
					List.of(new FragmentPair("generatedAtTime", value))),
					immutable, 0, List.of());
			return FragmentEntity.fromLdesFragment(ldesFragment);
		}

		private List<FragmentEntity> firstImmutableSecondOtherView() {
			return List.of(
					createLdesFragmentEntity(true, VIEW_NAME, FIRST_VALUE),
					createLdesFragmentEntity(false, new ViewName("c", "otherView"), SECOND_VALUE),
					createLdesFragmentEntity(false, VIEW_NAME, THIRD_VALUE));
		}

		private List<FragmentEntity> firstImmutable() {
			return List.of(
					createLdesFragmentEntity(true, VIEW_NAME, FIRST_VALUE),
					createLdesFragmentEntity(false, VIEW_NAME, SECOND_VALUE),
					createLdesFragmentEntity(false, VIEW_NAME, THIRD_VALUE));
		}

	}

}
