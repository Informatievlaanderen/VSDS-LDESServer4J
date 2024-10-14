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

-- remove pagination counting triggers and functions
drop trigger page_member_ai on page_members;
drop function on_page_member_inserted;

-- create function for pagination counting after page members inserted
create function on_page_members_inserted() returns trigger language plpgsql as $$
declare
  _stats view_stats%ROWTYPE;
  -- _count bigint;
begin
  -- select count(*) into _count from bucketized;
  -- raise WARNING 'on_page_members_inserted: % records in "bucketized" table', _count;
  for _stats in
    select b.view_id, count(distinct(b.member_id))::bigint, 0::bigint, max(b.member_id), 0::bigint
    from bucketized b
    where (
      select count(pm.member_id)
      from page_members pm
      where pm.view_id = b.view_id and pm.member_id = b.member_id
    ) = 1
    group by b.view_id
  loop
    update view_stats vs set
      bucketized_count = vs.bucketized_count + _stats.bucketized_count,
      bucketized_last_id = greatest(vs.bucketized_last_id,_stats.bucketized_last_id)
    where view_id = _stats.view_id;
  end loop;
  return null;
end
$$;

-- update stats row after page members inserted
create trigger page_members_ai after insert on page_members
referencing new table as bucketized
for statement execute procedure on_page_members_inserted();


-- remove pagination counting triggers and functions
drop trigger page_member_bu on page_members;
drop function on_page_member_updating;

-- create function for pagination counting after page members updated
create function on_page_members_updated() returns trigger language plpgsql as $$
declare
  _stats view_stats%ROWTYPE;
  -- _count bigint;
begin
  -- select count(*) into _count from paginated;
  -- raise WARNING 'on_page_members_updated: % records in "paginated" table', _count;
  for _stats in
    select p.view_id, 0::bigint, count(distinct(p.member_id))::bigint, 0::bigint, max(p.member_id)
    from paginated p
    join former f on f.bucket_id = p.bucket_id and f.member_id = p.member_id
    where f.page_id is null and p.page_id is not null and (
      select count(pm.member_id) from page_members pm where pm.view_id = p.view_id and pm.member_id = p.member_id
    ) = 1
    group by p.view_id
  loop
    update view_stats vs set
      paginated_count = vs.paginated_count + _stats.paginated_count,
      paginated_last_id = greatest(vs.paginated_last_id,_stats.paginated_last_id)
    where view_id = _stats.view_id;
  end loop;
  return null;
end
$$;

-- update stats row after page members updated
create trigger page_members_au after update on page_members
referencing old table as former new table as paginated
for statement execute procedure on_page_members_updated();
