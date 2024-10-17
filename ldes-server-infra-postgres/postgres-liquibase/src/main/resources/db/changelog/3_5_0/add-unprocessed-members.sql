create view unprocessed_members as
SELECT m.member_id, m.subject, m.version_of, m.timestamp,
c.name, c.version_of_path, c.timestamp_path, c.create_versions, m.member_model,
c.collection_id, v.view_id
FROM members m
join collections c on c.collection_id = m.collection_id
JOIN views v ON v.collection_id = c.collection_id
JOIN view_stats vs ON vs.view_id = v.view_id
WHERE m.member_id > vs.bucketized_last_id
AND m.xmin::text::bigint < pg_snapshot_xmin(pg_current_snapshot())::text::bigint
;
