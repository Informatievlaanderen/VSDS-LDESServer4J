package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.repository.LdesFragmentEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LdesFragmentMongoRepositoryTest {

	private static final String COLLECTION_NAME = "mobility-hindrances";

	private static final String VIEW_NAME = "view";
	private static final String FIRST_VALUE = "2020-12-28T09:36:37.127Z";
	private static final String SECOND_VALUE = "2020-12-29T09:36:37.127Z";
	private static final String THIRD_VALUE = "2020-12-30T09:36:37.127Z";

	private final LdesFragmentEntityRepository ldesFragmentEntityRepository = mock(LdesFragmentEntityRepository.class);

	private final LdesFragmentMongoRepository ldesFragmentMongoRepository = new LdesFragmentMongoRepository(
			ldesFragmentEntityRepository);

	@ParameterizedTest
	@ArgumentsSource(LdesFragmentEntityListProvider.class)
	void when_RetrieveOpenFragment_FirstFragmentThatIsOpenAndBelongsToCollectionIsReturned(
			List<LdesFragmentEntity> entitiesInRepository, String expectedFragmentId) {
		when(ldesFragmentEntityRepository
				.findAllByFragmentInfoImmutableAndFragmentInfoCollectionNameAndFragmentInfoViewName(false,
						COLLECTION_NAME, VIEW_NAME))
				.thenReturn(entitiesInRepository.stream()
						.filter(ldesFragmentEntity -> !ldesFragmentEntity.isImmutable()).collect(Collectors.toList()));

		Optional<LdesFragment> ldesFragment = ldesFragmentMongoRepository.retrieveOpenFragment(COLLECTION_NAME,
				VIEW_NAME,
				List.of());

		assertTrue(ldesFragment.isPresent());
		assertEquals(expectedFragmentId, ldesFragment.get().getFragmentId());
	}

	@Test
	void when_RetrieveInitialFragment_FirstFragmentThatBelongsToCollectionIsReturned() {
		when(ldesFragmentEntityRepository.findAll()).thenReturn(LdesFragmentEntityListProvider.allMutable());
		String expectedFragmentId = String.format("http://localhost:8080/%s?generatedAtTime=%s", COLLECTION_NAME,
				FIRST_VALUE);

		Optional<LdesFragment> view = ldesFragmentMongoRepository.retrieveInitialFragment(COLLECTION_NAME);

		assertTrue(view.isPresent());
		assertEquals(expectedFragmentId, view.get().getFragmentId());
	}

	@Test
	void when_RetrieveAllFragments_AllFragmentsAreReturned() {
		List<LdesFragmentEntity> ldesFragmentEntities = LdesFragmentEntityListProvider.allMutable();
		when(ldesFragmentEntityRepository.findAll()).thenReturn(ldesFragmentEntities);

		List<LdesFragment> ldesFragments = ldesFragmentMongoRepository.retrieveAllFragments();

		assertEquals(ldesFragmentEntities.size(), ldesFragments.size());
		assertEquals(ldesFragmentEntities.stream().map(LdesFragmentEntity::getId).collect(Collectors.toList()),
				ldesFragments.stream().map(LdesFragment::getFragmentId).collect(Collectors.toList()));
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

		private List<LdesFragmentEntity> firstImmutableSecondOtherView() {
			return List.of(
					createLdesFragmentEntity(true, COLLECTION_NAME, FIRST_VALUE),
					createLdesFragmentEntity(false, "other-view", SECOND_VALUE),
					createLdesFragmentEntity(false, COLLECTION_NAME, THIRD_VALUE));
		}

		protected static List<LdesFragmentEntity> allMutable() {
			return List.of(
					createLdesFragmentEntity(false, COLLECTION_NAME, FIRST_VALUE),
					createLdesFragmentEntity(false, COLLECTION_NAME, SECOND_VALUE),
					createLdesFragmentEntity(false, COLLECTION_NAME, THIRD_VALUE));
		}

		private List<LdesFragmentEntity> firstImmutable() {
			return List.of(
					createLdesFragmentEntity(true, COLLECTION_NAME, FIRST_VALUE),
					createLdesFragmentEntity(false, COLLECTION_NAME, SECOND_VALUE),
					createLdesFragmentEntity(false, COLLECTION_NAME, THIRD_VALUE));
		}

		private static LdesFragmentEntity createLdesFragmentEntity(boolean immutable,
				String collectionName,
				String value) {
			String fragmentId = String.format("http://localhost:8080/%s?generatedAtTime=%s", collectionName,
					value);
			FragmentInfo fragmentInfo = new FragmentInfo(collectionName, VIEW_NAME,
					List.of());
			fragmentInfo.setImmutable(immutable);
			return new LdesFragmentEntity(fragmentId, fragmentInfo, List.of(),
					List.of());
		}

	}

}