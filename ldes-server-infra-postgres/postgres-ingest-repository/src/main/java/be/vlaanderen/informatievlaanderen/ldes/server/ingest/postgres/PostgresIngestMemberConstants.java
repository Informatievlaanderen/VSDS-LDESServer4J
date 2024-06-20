package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres;

import org.apache.jena.riot.Lang;

public class PostgresIngestMemberConstants {
    private PostgresIngestMemberConstants() {
    }
    public static final Lang SERIALISATION_LANG = Lang.RDFPROTO;
    public static final String LDES_SERVER_DELETED_MEMBERS_COUNT = "ldes_server_deleted_members_count";
}
