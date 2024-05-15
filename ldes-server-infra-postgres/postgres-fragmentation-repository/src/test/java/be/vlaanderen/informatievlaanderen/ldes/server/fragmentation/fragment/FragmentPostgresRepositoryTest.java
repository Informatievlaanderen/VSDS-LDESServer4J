package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.FragmentPostgresRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.FragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.FragmentEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

class FragmentPostgresRepositoryTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final String FIRST_VALUE = "2020-12-28T09:36:37.127Z";
	private static final String SECOND_VALUE = "2020-12-29T09:36:37.127Z";
	private static final String THIRD_VALUE = "2020-12-30T09:36:37.127Z";

	private final FragmentEntityRepository ldesFragmentEntityRepository = Mockito.mock(FragmentEntityRepository.class);
	private final FragmentPostgresRepository ldesFragmentMongoRepository = new FragmentPostgresRepository(
			ldesFragmentEntityRepository);

	@ParameterizedTest
	@ArgumentsSource(LdesFragmentEntityListProvider.class)
	void when_RetrieveOpenFragment_FirstFragmentThatIsOpenAndBelongsToCollectionIsReturned(
			List<FragmentEntity> entitiesInRepository, String expectedFragmentId) {
		Mockito.when(ldesFragmentEntityRepository
				.findAllByImmutableAndViewName(false,
						VIEW_NAME.asString()))
				.thenReturn(entitiesInRepository.stream()
						.filter(ldesFragmentEntity -> !ldesFragmentEntity.isImmutable())
						.filter(ldesFragmentEntity -> ldesFragmentEntity.getViewName()
								.equals(VIEW_NAME.asString()))
						.toList());

		Optional<Fragment> ldesFragment = ldesFragmentMongoRepository.retrieveMutableFragment(
				VIEW_NAME.asString(),
				List.of());

		Assertions.assertTrue(ldesFragment.isPresent());
		Assertions.assertEquals(expectedFragmentId, ldesFragment.get().getFragmentIdString());
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
			Fragment fragment = new Fragment(new LdesFragmentIdentifier(viewName,
					List.of(new FragmentPair("generatedAtTime", value))),
					immutable, 0, List.of(), null);
			return FragmentEntity.fromLdesFragment(fragment);
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
