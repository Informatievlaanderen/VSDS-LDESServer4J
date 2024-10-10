-- add last ID columns
ALTER TABLE "view_stats" ADD COLUMN "bucketized_last_id" bigint NOT NULL default 0;
ALTER TABLE "view_stats" ADD COLUMN "paginated_last_id" bigint NOT NULL default 0;
ALTER TABLE "view_stats" ADD PRIMARY KEY ("view_id");

-- initialize last ID values
UPDATE view_stats vs SET bucketized_last_id =coalesce((
  select max(distinct(member_id)) from page_members pm WHERE pm.view_id = vs.view_id
), (0)::bigint);
UPDATE view_stats vs SET paginated_last_id = coalesce((
  select max(distinct(member_id)) from page_members pm WHERE pm.view_id = vs.view_id and pm.page_id is not null
), (0)::bigint);

-- remove pagination counting triggers and functions
drop trigger page_member_ai on page_members;
drop function on_page_member_inserted;

-- create function for pagination counting after page members inserted
create function on_page_members_inserted() returns trigger language plpgsql as $$
declare
  _stats view_stats%ROWTYPE;
begin
  for _stats in
    select view_id, count(distinct(member_id))::bigint, 0::bigint, max(member_id), 0::bigint
    from bucketized
    group by view_id
  loop
    update view_stats set
      bucketized_count = bucketized_count + _stats.bucketized_count,
      bucketized_last_id = _stats.bucketized_last_id
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
begin
  for _stats in
    select view_id, 0::bigint, count(distinct(member_id))::bigint, 0::bigint, max(member_id)
    from paginated where page_id is not null
    group by view_id
  loop
    update view_stats set
      paginated_count = paginated_count + _stats.paginated_count,
      paginated_last_id = _stats.paginated_last_id
    where view_id = _stats.view_id;
  end loop;
  return null;
end
$$;

-- update stats row after page members updated
create trigger page_members_au after update on page_members
referencing new table as paginated
for statement execute procedure on_page_members_updated();
