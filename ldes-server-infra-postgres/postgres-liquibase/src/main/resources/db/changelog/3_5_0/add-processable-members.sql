create view processable_members as
SELECT *
FROM members
WHERE xmin::text::bigint < pg_snapshot_xmin(pg_current_snapshot())::text::bigint
;
