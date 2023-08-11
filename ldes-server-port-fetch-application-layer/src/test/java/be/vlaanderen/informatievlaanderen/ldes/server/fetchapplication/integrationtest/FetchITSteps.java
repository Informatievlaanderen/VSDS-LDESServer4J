package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.integrationtest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.valueobjects.TreeNodeDto;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFWriter;

import java.net.URISyntaxException;
import java.util.*;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class FetchITSteps extends FetchIntegrationTest {

	private TreeNodeDto treeNodeDto;

	@DataTableType
	public EventStream EventStreamEntryTransformer(Map<String, String> row) {
		return new EventStream(
				row.get("collection"),
				row.get("timestampPath"),
				row.get("versionOfPath"),
				row.get("memberType"));
	}

	@DataTableType
	public ShaclShape ShaclShapeEntryTransformer(Map<String, String> row) throws URISyntaxException {
		return new ShaclShape(
				row.get("collection"),
				readModelFromFile(row.get("shacl")));
	}

	@DataTableType
	public DcatView OptionalDcatViewEntryTransformer(Map<String, String> row) throws URISyntaxException {
		return DcatView.from(ViewName.fromString(row.get("viewName")), readModelFromFile(row.get("dcat")));
	}

	@DataTableType
	public MemberAllocatedEvent MemberAllocatedEventEntryTransformer(Map<String, String> row) {
		return new MemberAllocatedEvent(
				row.get("memberId"),
				row.get("collectionName"),
				row.get("viewName"),
				row.get("fragmentId"));
	}

	@DataTableType
	public Member MemberEventEntryTransformer(Map<String, String> row) throws URISyntaxException {
		return new Member(
				row.get("memberId"),
				row.get("collectionName"),
				0L,
				readModelFromFile(row.get("model")));
	}

	@DataTableType(replaceWithEmptyString = "[blank]")
	public Fragment FragmentEntryTransformer(Map<String, String> row) {
		return new Fragment(
				LdesFragmentIdentifier.fromFragmentId(row.get("fragmentId")),
				Boolean.parseBoolean(row.get("immutable")),
				0, // number of members is irrelevant for fetching
				row.get("relations").equals("") ? List.of()
						: List.of(new TreeRelation("",
								LdesFragmentIdentifier.fromFragmentId(row.get("relations")), "", "",
								GENERIC_TREE_RELATION)));
	}

	@Given("The following EventStream")
	public void theFollowingEventStream(EventStream eventStream) {
		applicationEventPublisher.publishEvent(new EventStreamCreatedEvent(eventStream));
	}

	@And("The following Allocations")
	public void theFollowingAllocations(List<MemberAllocatedEvent> memberAllocatedEvents) {
		memberAllocatedEvents.forEach(applicationEventPublisher::publishEvent);
	}

	@And("the following Fragments can be retrieved from the FragmentRepository")
	public void theFollowingFragmentsCanBeRetrievedFromTheFragmentRepository(List<Fragment> fragments) {
		fragments.forEach(fragment -> when(fragmentRepository.retrieveFragment(fragment.getFragmentId()))
				.thenReturn(Optional.of(fragment)));
	}

	@And("the following Members can be retrieved from the MemberRepository")
    public void theFollowingMembersCanBeRetrievedFromTheMemberRepository(List<Member> members) {
        when(memberRepository.findAllByIds(members.stream().map(Member::getId).toList())).thenReturn(members);
    }

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}

	@And("The following shacl")
	public void theFollowingShacl(ShaclShape shaclShape) {
		applicationEventPublisher.publishEvent(new ShaclChangedEvent(shaclShape));
	}

	@And("the following dcat can be retrieved from the DcatViewService")
    public void theFollowingDcatCanBeRetrievedFromTheDcatViewService(DcatView dcatView) {
        when(dcatViewService.findByViewName(dcatView.getViewName())).thenReturn(Optional.of(dcatView));
    }

	@When("^The TreeNodeDTO with for LdesFragmentRequest with viewName ([^ ]+) and fragmentPairs ([^ ]+) is fetched")
	public void theTreeNodeDTOWithForLdesFragmentRequestWithViewNameViewNameAndFragmentPairsFragmentPairsIsFetched(
			String viewName, String fragmentPairsSerialized) {
		List<FragmentPair> fragmentPairs;
		if (!fragmentPairsSerialized.equals("null")) {
			fragmentPairs = Lists
					.partition(Arrays.stream(fragmentPairsSerialized.split(",")).toList(), 2)
					.stream()
					.map(fragmentpair -> new FragmentPair(fragmentpair.get(0), fragmentpair.get(1)))
					.toList();
		} else {
			fragmentPairs = List.of();
		}
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(ViewName.fromString(viewName), fragmentPairs);
		treeNodeDto = treeNodeFetcher.getFragment(ldesFragmentRequest);
	}

	@Then("^The Model of the TreeNodeDTO is the same as in ([^ ]+)")
	public void theModelOfTheTreeNodeDTOIsTheSameAsInExpectedModel(String fileName) throws URISyntaxException {
		System.out.println(RDFWriter.source(treeNodeDto.getModel()).lang(Lang.TURTLE).asString());
		assertTrue(treeNodeDto.getModel().isIsomorphicWith(readModelFromFile(fileName)));
	}
}
