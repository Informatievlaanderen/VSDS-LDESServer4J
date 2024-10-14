
-- create function for pagination counting after page members updated
create function on_page_members_updated() returns trigger language plpgsql as $$
declare
  _stats view_stats%ROWTYPE;
begin
  -- for each paginated member
  for _stats in
    select p.view_id, 0::bigint, count(distinct(p.member_id))::bigint, 0::bigint, max(p.member_id)
    from paginated p
    join former f on f.bucket_id = p.bucket_id and f.member_id = p.member_id
    where f.page_id is null and p.page_id is not null and (
      select count(pm.member_id) from page_members pm where pm.view_id = p.view_id and pm.member_id = p.member_id
    ) = 1
    group by p.view_id
  loop
    -- update number of members paginated and remember the last ID
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
