package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.RootFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FragmentationStrategyCreatorImplTest {
	private static final Map<String, String> TIMEBASED_PROPERTIES = Map.of("timebasedProperty", "time");
	private static final Map<String, String> GEOSPATIAL_PROPERTIES = Map.of("geospatialProperty", "geo");
	private static final String GEOSPATIAL = "GeospatialFragmentation";
	private static final String TIMEBASED = "TimebasedFragmentation";
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "viewName");

	private final ApplicationContext applicationContext = mock(ApplicationContext.class);
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final RootFragmentCreator rootFragmentCreator = mock(RootFragmentCreator.class);
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private FragmentationStrategyCreatorImpl fragmentationStrategyCreator;

	@BeforeEach
	void setUp() {
		fragmentationStrategyCreator = new FragmentationStrategyCreatorImpl(
				applicationContext, ldesFragmentRepository, rootFragmentCreator,
				memberRepository, applicationEventPublisher);
	}

	@Test
	void when_ViewSpecificationFragmentationConfigIsNull_FragmentationStrategyImplIsReturned() {
		ViewSpecification viewSpecification = new ViewSpecification(VIEW_NAME, List.of(), List.of());

		FragmentationStrategy fragmentationStrategy = fragmentationStrategyCreator
				.createFragmentationStrategyForView(viewSpecification);

		assertTrue(fragmentationStrategy instanceof FragmentationStrategyImpl);
		InOrder inOrder = inOrder(applicationContext, rootFragmentCreator);
		inOrder.verify(rootFragmentCreator,
				times(1)).createRootFragmentForView(viewSpecification.getName());
		inOrder.verify(applicationContext, times(1)).getBean(NonCriticalTasksExecutor.class);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_ViewSpecificationFragmentationConfigIsNotNull_WrappedFragmentationStrategyIsReturned() {
		FragmentationStrategyWrapper timebasedFragmentationStrategyWrapper = mock(FragmentationStrategyWrapper.class);
		when(applicationContext.getBean(TIMEBASED)).thenReturn(timebasedFragmentationStrategyWrapper);
		FragmentationStrategy timebasedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(timebasedFragmentationStrategyWrapper.wrapFragmentationStrategy(eq(applicationContext),
				any(),
				eq(new ConfigProperties(TIMEBASED_PROPERTIES))))
				.thenReturn(timebasedFragmentationStrategy);

		FragmentationStrategyWrapper geospatialFragmentationStrategyWrapper = mock(FragmentationStrategyWrapper.class);
		when(applicationContext.getBean(GEOSPATIAL)).thenReturn(geospatialFragmentationStrategyWrapper);
		FragmentationStrategy geospatialFragmentationStrategy = mock(FragmentationStrategy.class);
		when(geospatialFragmentationStrategyWrapper.wrapFragmentationStrategy(applicationContext,
				timebasedFragmentationStrategy, new ConfigProperties(GEOSPATIAL_PROPERTIES)))
				.thenReturn(geospatialFragmentationStrategy);

		ViewSpecification viewSpecification = getViewSpecification();
		FragmentationStrategy fragmentationStrategy = fragmentationStrategyCreator
				.createFragmentationStrategyForView(viewSpecification);

		assertEquals(geospatialFragmentationStrategy, fragmentationStrategy);
		InOrder inOrder = inOrder(applicationContext, rootFragmentCreator);
		inOrder.verify(rootFragmentCreator,
				times(1)).createRootFragmentForView(viewSpecification.getName());
		inOrder.verify(applicationContext, times(1)).getBean(TIMEBASED);
		inOrder.verify(applicationContext, times(1)).getBean(GEOSPATIAL);
		inOrder.verifyNoMoreInteractions();

	}

	private ViewSpecification getViewSpecification() {
		FragmentationConfig geospatialConfig = getFragmentationConfig(GEOSPATIAL, GEOSPATIAL_PROPERTIES);
		FragmentationConfig timebasedConfig = getFragmentationConfig(TIMEBASED, TIMEBASED_PROPERTIES);
		return new ViewSpecification(VIEW_NAME, List.of(), List.of(geospatialConfig, timebasedConfig));
	}

	private FragmentationConfig getFragmentationConfig(String name, Map<String, String> config) {
		FragmentationConfig geospatialConfig = new FragmentationConfig();
		geospatialConfig.setName(name);
		geospatialConfig.setConfig(config);
		return geospatialConfig;
	}
}
