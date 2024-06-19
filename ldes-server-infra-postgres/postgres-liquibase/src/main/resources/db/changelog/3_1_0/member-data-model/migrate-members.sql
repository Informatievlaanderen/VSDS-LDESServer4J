insert into members (subject, old_id, collection_id, version_of, timestamp, transaction_id, is_in_event_source, member_model)
select SUBSTRING(ingest_ldesmember.id, strpos(ingest_ldesmember.id, '/')+1, length(ingest_ldesmember.id)), ingest_ldesmember.id, collections.collection_id, ingest_ldesmember.version_of, ingest_ldesmember.timestamp, ingest_ldesmember.transaction_id, ingest_ldesmember.is_in_event_source, ingest_ldesmember.model
from ingest_ldesmember left join collections on ingest_ldesmember.collection_name=collections.name