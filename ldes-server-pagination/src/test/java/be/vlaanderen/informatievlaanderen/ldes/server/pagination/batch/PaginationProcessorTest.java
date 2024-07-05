package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.MemberPaginationService;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.MemberPaginationServiceCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
//TODO: move to other module
//
//@SpringBootTest
//@ContextConfiguration(classes = { PageRelationProcessor.class, MemberPaginationServiceCreator.class, ViewBucketisationService.class })
//class PaginationProcessorTest {
//	@MockBean
//	FragmentRepository fragmentRepository;
//	@Autowired
//	PaginationProcessor processor;
//	@Autowired
//	ApplicationEventPublisher eventPublisher;
//
//
//	@BeforeEach
//	void setup() {
//		processor.paginationServices.clear();
//	}
//
//	@Test
//	void eventHandling_ViewCreation_and_deletion() {
//		ViewName v1 = viewName("v1");
//		ViewName v2 = viewName("v2");
//
//		eventPublisher.publishEvent(new ViewInitializationEvent(this, viewSpecification(v1)));
//
//		assertTrue(processor.getPaginationServices().containsKey(v1.asString()));
//		eventPublisher.publishEvent(new ViewAddedEvent(this, viewSpecification(v2)));
//		assertTrue(processor.getPaginationServices().containsKey(v2.asString()));
//
//		eventPublisher.publishEvent(new ViewDeletedEvent(this, v1));
//		assertFalse(processor.getPaginationServices().containsKey(v1.asString()));
//		assertEquals(1, processor.getPaginationServices().size());
//
//		eventPublisher.publishEvent(new ViewDeletedEvent(this, v1));
//		assertFalse(processor.getPaginationServices().containsKey(v1.asString()));
//		assertEquals(1, processor.getPaginationServices().size());
//	}
//
//	@Test
//	void processMembers_emptyList() {
//		ViewName v1 = viewName("v1");
//		MemberPaginationService paginationService = mock(MemberPaginationService.class);
//
//		processor.paginationServices.put(v1.asString(), paginationService);
//
//		var result = processor.process(List.of());
//
//		assertNull(result);
//	}
//
//	@Test
//	void processMembers_noPaginationService() {
//		ViewName v1 = viewName("v1");
////		List<BucketisedMember> bucketisedMembers = List.of(new BucketisedMember("x/1", v1, v1.asString()));
//
////		assertThrows(NoSuchElementException.class, ()-> processor.process(bucketisedMembers));
//	}
//
//	@Test
//	void processMembers() {
//		ViewName v1 = viewName("v1");
//		MemberPaginationService paginationService = mock(MemberPaginationService.class);
//		processor.paginationServices.put(v1.asString(), paginationService);
//
////		List<BucketisedMember> bucketisedMembers = List.of(new BucketisedMember("x/1", v1, v1.asString()));
//
////		processor.process(bucketisedMembers);
//
////		verify(paginationService, times(1)).paginateMember(bucketisedMembers);
//	}
//
//
//
//	private ViewName viewName(String view) {
//		final String COLLECTION = "es";
//		return new ViewName(COLLECTION, view);
//	}
//
//	private ViewSpecification viewSpecification(ViewName view) {
//		return new ViewSpecification(view, List.of(), List.of(), 10);
//	}
//}
