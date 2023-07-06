package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
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
	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);
	private final RootFragmentCreator rootFragmentCreator = Mockito.mock(RootFragmentCreator.class);
	private final AllocationRepository allocationRepository = mock(AllocationRepository.class);
	private FragmentationStrategyCreatorImpl fragmentationStrategyCreator;

	@BeforeEach
	void setUp() {
		fragmentationStrategyCreator = new FragmentationStrategyCreatorImpl(
				applicationContext, fragmentRepository, rootFragmentCreator,
				allocationRepository);
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
		FragmentationStrategyWrapper timebasedFragmentationStrategyWrapper = Mockito.mock(FragmentationStrategyWrapper.class);
		when(applicationContext.getBean(TIMEBASED)).thenReturn(timebasedFragmentationStrategyWrapper);
		FragmentationStrategy timebasedFragmentationStrategy = Mockito.mock(FragmentationStrategy.class);
		when(timebasedFragmentationStrategyWrapper.wrapFragmentationStrategy(eq(applicationContext),
				any(),
				eq(new ConfigProperties(TIMEBASED_PROPERTIES))))
				.thenReturn(timebasedFragmentationStrategy);

		FragmentationStrategyWrapper geospatialFragmentationStrategyWrapper = Mockito.mock(FragmentationStrategyWrapper.class);
		when(applicationContext.getBean(GEOSPATIAL)).thenReturn(geospatialFragmentationStrategyWrapper);
		FragmentationStrategy geospatialFragmentationStrategy = Mockito.mock(FragmentationStrategy.class);
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
