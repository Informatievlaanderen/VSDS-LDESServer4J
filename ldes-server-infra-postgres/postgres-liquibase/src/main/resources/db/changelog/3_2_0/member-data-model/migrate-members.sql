insert into members (subject, old_id, collection_id, version_of, timestamp, transaction_id, is_in_event_source,
                     member_model)
select SUBSTRING(i.id, strpos(i.id, '/') + 1, length(i.id)),
       i.id,
       c.collection_id,
       i.version_of,
       i.timestamp,
       i.transaction_id,
       i.is_in_event_source,
       i.model
from ingest_ldesmember i
         left join collections c on i.collection_name = c.name