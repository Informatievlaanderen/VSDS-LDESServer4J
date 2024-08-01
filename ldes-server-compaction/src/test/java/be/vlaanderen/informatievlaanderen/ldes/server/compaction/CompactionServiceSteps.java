package be.vlaanderen.informatievlaanderen.ldes.server.compaction;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.*;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SuppressWarnings("java:S3415")
public class CompactionServiceSteps extends CompactionIntegrationTest {

    @DataTableType
    public ViewSpecification ViewSpecificationEntryTransformer(Map<String, String> row) {
        return new ViewSpecification(
                ViewName.fromString(row.get("viewName")),
                List.of(), List.of(), Integer.parseInt(row.get("pageSize")));
    }

    @DataTableType
    public FragmentAllocations FragmentAllocationsListEntryTransformer(Map<String, String> row) {
        List<MemberAllocation> memberAllocations = new ArrayList<>();
        String fragmentIdentifier = row.get("fragmentIdentifier");
        for (String memberId : row.get("members").split(",")) {
            memberAllocations.add(new MemberAllocation(fragmentIdentifier + "/" + memberId, "mobility-hindrances",
                    "by-page", fragmentIdentifier, memberId));
        }
        return new FragmentAllocations(fragmentIdentifier, memberAllocations);
    }

    @DataTableType
    public Member MemberFragmentationsEntryTransformer(Map<String, String> row) {
        return new Member(Long.parseLong(row.get("pageId")),
                row.get("collection"),
                row.get("bucketDescriptor"),
                Integer.parseInt(row.get("amount")));
    }

    @DataTableType(replaceWithEmptyString = "[blank]")
    public Page FragmentEntryTransformer(Map<String, String> row) {
        return new Page(
                row.get("bucket"),
                row.get("view"),
                Boolean.parseBoolean(row.get("immutable")),
                row.get("url"));
    }

    public class Page {
        private final String bucketDescription;
        private final String viewName;
        private final boolean immutable;
        private final String partialUrl;

        public Page(String bucketDescription, String viewName, boolean immutable, String partialUrl) {
            this.bucketDescription = bucketDescription;
            this.viewName = viewName;
            this.immutable = immutable;
            this.partialUrl = partialUrl;
        }
    }

    public class Member {
        private final long pageId;
        private final String collectionName;
        private final String bucketDesc;
        private final int amount;

        public Member(long pageId, String collectionName, String bucketDesc, int amount) {
            this.pageId = pageId;
            this.collectionName = collectionName;
            this.bucketDesc = bucketDesc;
            this.amount = amount;
        }
    }

    @Given("a collection with the following name {string}")
    public void aViewWithTheFollowingProperties(String name) {
        applicationEventPublisher.publishEvent(new EventStreamCreatedEvent(
                new EventStream(name, "http://example.com",
                        "http://example.com", true)));
    }

    @Given("a view with the following properties")
    public void aViewWithTheFollowingProperties(ViewSpecification viewSpecification) {
        applicationEventPublisher.publishEvent(new ViewAddedEvent(viewSpecification));
    }

    @And("the following Fragments are available")
    public void theFollowingPagesAreAvailable(List<Page> pages) {
        AtomicLong lastPageId = new AtomicLong(0L);
        pages.forEach(page -> {
            MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                    "bucket", page.bucketDescription,
                    "viewName", page.viewName
            ));
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update("INSERT INTO buckets (bucket, view_id) SELECT :bucket, v.view_id FROM view_names v WHERE v.view_name = :viewName",
                    params, keyHolder, new String[]{"bucket_id"});

            MapSqlParameterSource pageParams = new MapSqlParameterSource(Map.of(
                    "bucketId", keyHolder.getKey(),
                    "immutable", page.immutable,
                    "partialUrl", page.partialUrl
            ));
            jdbcTemplate.update("INSERT INTO pages (bucket_id, immutable, partial_url) VALUES (:bucketId, :immutable, :partialUrl)", pageParams, keyHolder, new String[]{"page_id"});
            if (lastPageId.get() != 0L) {

                MapSqlParameterSource relationParams = new MapSqlParameterSource(Map.of(
                        "from", lastPageId.get(),
                        "to", keyHolder.getKey(),
                        "type", GENERIC_TREE_RELATION
                ));
                jdbcTemplate.update("INSERT INTO page_relations (from_page_id, to_page_id, relation_type) VALUES (:from, :to, :type)", relationParams);
            }
            lastPageId.set(keyHolder.getKeyAs(Long.class));
        });
    }

    @And("the following members are present")
    public void theFollowingAllocationsArePresent(List<Member> members) {
//        mobility-hindrances/by-page
        members.forEach(memberGroup -> {
            for(int i = 0; i < memberGroup.amount; i++) {
                MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                        "oldId", UUID.randomUUID().toString(),
                        "subject", "http://example.com",
                        "collId", 1L,
                        "versionOf", "http://example.com",
                        "timestamp", LocalDateTime.now(),
                        "transactionId", UUID.randomUUID().toString(),
                        "inEventSource", true,
                        "model", "http://example.com"
                ));
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update("INSERT INTO members (old_id, subject, collection_id, version_of, timestamp, transaction_id, is_in_event_source, member_model) " +
                                "VALUES (:oldId, :subject, :collId, :versionOf, :timestamp, :transactionId, :inEventSource, :model)",
                        params, keyHolder, new String[]{"member_id"});

                MapSqlParameterSource memberParams = new MapSqlParameterSource(Map.of(
                        "memberId", keyHolder.getKey(),
                        "pageId", memberGroup.pageId
                ));
                jdbcTemplate.update("INSERT INTO page_members (member_id, bucket_id, page_id) " +
                                "SELECT :memberId, b.bucket_id, p.page_id FROM pages p JOIN buckets b ON p.bucket_id = b.bucket_id WHERE p.page_id = :pageId",
                        memberParams);
            }
        });


    }

//    private Stream<CompactionCandidate> getAllocationAggregates(List<FragmentAllocations> fragmentAllocations, ViewName viewName) {
//        return fragmentAllocations.stream()
//                .filter(fragmentAllocation -> {
//                    var fragmentId = LdesFragmentIdentifier.fromFragmentId(fragmentAllocation.fragmentId);
//                    return fragmentId.getViewName().equals(viewName);
//                })
//                .map(fragmentAllocation -> new CompactionCandidate(fragmentAllocation.fragmentId, fragmentAllocation.memberAllocations.size()));
//    }

    @And("verify there are {int} pages")
    public void verifyCreationOfTheFollowingFragments(int i) {
        assertThat(entityManager.createQuery("SELECT COUNT(*) FROM pages p").getSingleResult()).isEqualTo(i);
    }

    @And("verify update of predecessor relations")
    public void verifyUpdateOfPredecessorRelations(List<Long> ids) {
        var count = entityManager.createQuery("SELECT COUNT(*) FROM page_relations r JOIN pages p ON r.to_page = p WHERE p.id IN :ids")
                .setParameter("ids", ids).getSingleResult();
        assertThat(count).isEqualTo(0);
    }

    @And("verify fragmentation of members")
    public void verifyFragmentationOfMembers(List<MemberFragmentations> memberFragmentations) {
//        verify(eventConsumer, times(memberFragmentations.size())).consumeEvent(any(BulkMemberAllocatedEvent.class));
//        memberFragmentations.forEach(memberFragmentation -> {
//            verify(fragmentRepository).incrementNrOfMembersAdded(LdesFragmentIdentifier.fromFragmentId(memberFragmentation.fragmentId), memberFragmentation.memberIds.size());
//        });
    }

    @Then("wait for {int} seconds until compaction has executed at least once")
    public void waitForSecondsUntilCompactionHasExecutedAtLeastOnce(int secondsToWait) {
        await()
                .timeout(secondsToWait + 1, SECONDS)
                .pollDelay(secondsToWait, SECONDS)
                .untilAsserted(() -> assertThat(true).isTrue());
    }

    public record FragmentAllocations(String fragmentId, List<MemberAllocation> memberAllocations) {
    }

    public record MemberFragmentations(String fragmentId, List<String> memberIds) {
    }

}
