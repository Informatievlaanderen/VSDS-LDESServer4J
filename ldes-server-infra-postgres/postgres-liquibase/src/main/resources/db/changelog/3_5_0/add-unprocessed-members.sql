-- create view to retrieve members needing bucketization
create view unprocessed_members as
select v.collection_id, v.view_id, m.member_id
from members m
join views v on v.collection_id = m.collection_id
join view_stats vs on vs.view_id = v.view_id
where m.member_id > vs.bucketized_last_id
and m.xmin::text::bigint < pg_snapshot_xmin(pg_current_snapshot())::text::bigint;
