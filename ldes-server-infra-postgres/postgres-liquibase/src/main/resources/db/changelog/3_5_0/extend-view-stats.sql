-- add last ID columns
ALTER TABLE "view_stats" ADD COLUMN "bucketized_last_id" bigint NOT NULL default 0;
ALTER TABLE "view_stats" ADD COLUMN "paginated_last_id" bigint NOT NULL default 0;
ALTER TABLE "view_stats" ADD PRIMARY KEY ("view_id");

-- initialize last ID values
UPDATE view_stats vs SET bucketized_last_id = coalesce(
  (select max(distinct(member_id))
   from page_members pm
   WHERE pm.view_id = vs.view_id),
  (0)::bigint
);

UPDATE view_stats vs SET paginated_last_id = coalesce(
  (select max(distinct(member_id))
   from page_members pm
   WHERE pm.view_id = vs.view_id and pm.page_id is not null),
  (0)::bigint
);
