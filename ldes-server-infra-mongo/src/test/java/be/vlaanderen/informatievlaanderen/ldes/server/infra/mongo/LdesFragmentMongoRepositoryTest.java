package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentEntityRepository;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LdesFragmentMongoRepositoryTest {

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
				.findAllByImmutableAndViewName(false,
						VIEW_NAME))
				.thenReturn(entitiesInRepository.stream()
						.filter(ldesFragmentEntity -> !ldesFragmentEntity.isImmutable())
						.filter(ldesFragmentEntity -> ldesFragmentEntity.getFragmentInfo().getViewName()
								.equals(VIEW_NAME))
						.collect(Collectors.toList()));

		Optional<LdesFragment> ldesFragment = ldesFragmentMongoRepository.retrieveOpenFragment(
				VIEW_NAME,
				List.of());

		assertTrue(ldesFragment.isPresent());
		assertEquals(expectedFragmentId, ldesFragment.get().getFragmentId());
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
					createLdesFragmentEntity(true, VIEW_NAME, FIRST_VALUE),
					createLdesFragmentEntity(false, "otherView", SECOND_VALUE),
					createLdesFragmentEntity(false, VIEW_NAME, THIRD_VALUE));
		}

		protected static List<LdesFragmentEntity> allMutable() {
			return List.of(
					createLdesFragmentEntity(false, VIEW_NAME, FIRST_VALUE),
					createLdesFragmentEntity(false, VIEW_NAME, SECOND_VALUE),
					createLdesFragmentEntity(false, VIEW_NAME, THIRD_VALUE));
		}

		private List<LdesFragmentEntity> firstImmutable() {
			return List.of(
					createLdesFragmentEntity(true, VIEW_NAME, FIRST_VALUE),
					createLdesFragmentEntity(false, VIEW_NAME, SECOND_VALUE),
					createLdesFragmentEntity(false, VIEW_NAME, THIRD_VALUE));
		}

		private static LdesFragmentEntity createLdesFragmentEntity(boolean immutable,
				String viewName,
				String value) {
			FragmentInfo fragmentInfo = new FragmentInfo(viewName, List.of(new FragmentPair("generatedAtTime", value)),
					immutable, LocalDateTime.now(), false);
			LdesFragment ldesFragment = new LdesFragment(fragmentInfo);
			return LdesFragmentEntity.fromLdesFragment(ldesFragment);
		}

	}

}