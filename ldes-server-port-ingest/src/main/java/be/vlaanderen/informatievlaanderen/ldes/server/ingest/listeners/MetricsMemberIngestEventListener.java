package be.vlaanderen.informatievlaanderen.ldes.server.ingest.listeners;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import io.micrometer.core.instrument.Metrics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MetricsMemberIngestEventListener {
    private static final String LDES_SERVER_INGESTED_MEMBERS_COUNT = "ldes_server_ingested_members_count";
    @EventListener
    private void handleSuccessfulMemberInsertion(Member member, String memberId) {
        Metrics.counter(LDES_SERVER_INGESTED_MEMBERS_COUNT).increment();
    }
}
