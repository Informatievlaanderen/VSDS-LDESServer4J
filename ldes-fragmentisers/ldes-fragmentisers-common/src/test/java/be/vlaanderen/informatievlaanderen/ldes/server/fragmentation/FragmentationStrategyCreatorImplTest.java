package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.RootFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyCreatorImpl.PAGINATION_FRAGMENTATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FragmentationStrategyCreatorImplTest {
	private static final Map<String, String> TIMEBASED_PROPERTIES = Map.of("timebasedProperty", "time");
	private static final Map<String, String> GEOSPATIAL_PROPERTIES = Map.of("geospatialProperty", "geo");
	private static final String GEOSPATIAL = "GeospatialFragmentation";
	private static final String TIMEBASED = "TimebasedFragmentation";
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "viewName");

	private ApplicationContext applicationContext;
	private RootFragmentCreator rootFragmentCreator;
	private FragmentationStrategyCreatorImpl fragmentationStrategyCreator;

	@BeforeEach
	void setUp() {
		applicationContext = mock(ApplicationContext.class);
		FragmentRepository fragmentRepository = mock(FragmentRepository.class);
		rootFragmentCreator = mock(RootFragmentCreator.class);
		ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
		fragmentationStrategyCreator = new FragmentationStrategyCreatorImpl(
				applicationContext, fragmentRepository, rootFragmentCreator,
				eventPublisher);
	}

	@Test
	void when_ViewSpecificationFragmentationConfigIsNull_FragmentationStrategyImplIsReturned() {
		ViewSpecification viewSpecification = new ViewSpecification(VIEW_NAME, List.of(), List.of(), 100);
		FragmentationStrategyWrapper paginationFragmentationStrategyWrapper = Mockito
				.mock(FragmentationStrategyWrapper.class);
		when(applicationContext.getBean(PAGINATION_FRAGMENTATION)).thenReturn(paginationFragmentationStrategyWrapper);
		FragmentationStrategy paginationFragmentationStrategy = Mockito.mock(FragmentationStrategy.class);
		when(paginationFragmentationStrategyWrapper.wrapFragmentationStrategy(eq(applicationContext),
				any(FragmentationStrategyImpl.class),
				eq(viewSpecification.getPaginationProperties())))
				.thenReturn(paginationFragmentationStrategy);

		FragmentationStrategy fragmentationStrategy = fragmentationStrategyCreator
				.createFragmentationStrategyForView(viewSpecification);

		assertEquals(paginationFragmentationStrategy, fragmentationStrategy);
		InOrder inOrder = inOrder(applicationContext, rootFragmentCreator);
		inOrder.verify(rootFragmentCreator).createRootFragmentForView(viewSpecification.getName());
		inOrder.verify(applicationContext).getBean(PAGINATION_FRAGMENTATION);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_ViewSpecificationFragmentationConfigIsNotNull_WrappedFragmentationStrategyIsReturned() {
		ViewSpecification viewSpecification = getViewSpecification();
		FragmentationStrategyWrapper paginationFragmentationStrategyWrapper = Mockito
				.mock(FragmentationStrategyWrapper.class);
		when(applicationContext.getBean(PAGINATION_FRAGMENTATION)).thenReturn(paginationFragmentationStrategyWrapper);
		FragmentationStrategy paginationFragmentationStrategy = Mockito.mock(FragmentationStrategy.class);
		when(paginationFragmentationStrategyWrapper.wrapFragmentationStrategy(eq(applicationContext),
				any(FragmentationStrategyImpl.class),
				eq(viewSpecification.getPaginationProperties())))
				.thenReturn(paginationFragmentationStrategy);
		FragmentationStrategyWrapper timebasedFragmentationStrategyWrapper = Mockito
				.mock(FragmentationStrategyWrapper.class);
		when(applicationContext.getBean(TIMEBASED)).thenReturn(timebasedFragmentationStrategyWrapper);
		FragmentationStrategy timebasedFragmentationStrategy = Mockito.mock(FragmentationStrategy.class);
		when(timebasedFragmentationStrategyWrapper.wrapFragmentationStrategy(applicationContext,
				paginationFragmentationStrategy,
				new ConfigProperties(TIMEBASED_PROPERTIES)))
				.thenReturn(timebasedFragmentationStrategy);

		FragmentationStrategyWrapper geospatialFragmentationStrategyWrapper = Mockito
				.mock(FragmentationStrategyWrapper.class);
		when(applicationContext.getBean(GEOSPATIAL)).thenReturn(geospatialFragmentationStrategyWrapper);
		FragmentationStrategy geospatialFragmentationStrategy = Mockito.mock(FragmentationStrategy.class);
		when(geospatialFragmentationStrategyWrapper.wrapFragmentationStrategy(applicationContext,
				timebasedFragmentationStrategy, new ConfigProperties(GEOSPATIAL_PROPERTIES)))
				.thenReturn(geospatialFragmentationStrategy);

		FragmentationStrategy fragmentationStrategy = fragmentationStrategyCreator
				.createFragmentationStrategyForView(viewSpecification);

		assertEquals(geospatialFragmentationStrategy, fragmentationStrategy);
		InOrder inOrder = inOrder(applicationContext, rootFragmentCreator);
		inOrder.verify(rootFragmentCreator).createRootFragmentForView(viewSpecification.getName());
		inOrder.verify(applicationContext).getBean(PAGINATION_FRAGMENTATION);
		inOrder.verify(applicationContext).getBean(TIMEBASED);
		inOrder.verify(applicationContext).getBean(GEOSPATIAL);
		inOrder.verifyNoMoreInteractions();

	}

	private ViewSpecification getViewSpecification() {
		FragmentationConfig geospatialConfig = getFragmentationConfig(GEOSPATIAL, GEOSPATIAL_PROPERTIES);
		FragmentationConfig timebasedConfig = getFragmentationConfig(TIMEBASED, TIMEBASED_PROPERTIES);
		return new ViewSpecification(VIEW_NAME, List.of(), List.of(geospatialConfig, timebasedConfig), 100);
	}

	private FragmentationConfig getFragmentationConfig(String name, Map<String, String> config) {
		FragmentationConfig geospatialConfig = new FragmentationConfig();
		geospatialConfig.setName(name);
		geospatialConfig.setConfig(config);
		return geospatialConfig;
	}
}
