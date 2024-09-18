package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.FragmentationStrategyCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.RootBucketCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FragmentationStrategyCreatorImplTest {
	private static final Map<String, String> TIMEBASED_PROPERTIES = Map.of("timebasedProperty", "time");
	private static final Map<String, String> GEOSPATIAL_PROPERTIES = Map.of("geospatialProperty", "geo");
	private static final String GEOSPATIAL = "GeospatialFragmentation";
	private static final String TIMEBASED = "TimebasedFragmentation";
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "viewName");

	private ApplicationContext applicationContext;
	private RootBucketCreator rootBucketCreator;

	private FragmentationStrategyCreatorImpl fragmentationStrategyCreator;

	@BeforeEach
	void setUp() {
		applicationContext = mock(ApplicationContext.class);
		rootBucketCreator = mock();
		fragmentationStrategyCreator = new FragmentationStrategyCreatorImpl(applicationContext);
	}

	@Test
	void when_ViewSpecificationFragmentationConfigIsNull_FragmentationStrategyImplIsReturned() {
		ViewSpecification viewSpecification = new ViewSpecification(VIEW_NAME, List.of(), List.of(), 100);

		FragmentationStrategy fragmentationStrategy = fragmentationStrategyCreator
				.createFragmentationStrategyForView(viewSpecification);

		assertThat(fragmentationStrategy).isOfAnyClassIn(FragmentationStrategyImpl.class);
		InOrder inOrder = inOrder(applicationContext, rootBucketCreator);
		inOrder.verify(rootBucketCreator).createRootBucketForView(viewSpecification.getName());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_ViewSpecificationFragmentationConfigIsNotNull_WrappedFragmentationStrategyIsReturned() {
		ViewSpecification viewSpecification = getViewSpecification();
		FragmentationStrategyWrapper timebasedFragmentationStrategyWrapper = Mockito
				.mock(FragmentationStrategyWrapper.class);
		when(applicationContext.getBean(TIMEBASED)).thenReturn(timebasedFragmentationStrategyWrapper);
		FragmentationStrategy timebasedFragmentationStrategy = Mockito.mock(FragmentationStrategy.class);
		when(timebasedFragmentationStrategyWrapper.wrapFragmentationStrategy(eq(applicationContext),
				any(FragmentationStrategyImpl.class),
				eq(new ConfigProperties(TIMEBASED_PROPERTIES))))
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
		InOrder inOrder = inOrder(applicationContext, rootBucketCreator);
		inOrder.verify(rootBucketCreator).createRootBucketForView(viewSpecification.getName());
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
