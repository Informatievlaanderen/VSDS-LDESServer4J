
-- create function for pagination counting after page members inserted
create function on_page_members_inserted() returns trigger language plpgsql as $$
declare
  _stats view_stats%ROWTYPE;
begin
  -- for each bucketized member
  for _stats in
    select b.view_id, count(distinct(b.member_id))::bigint, 0::bigint, max(b.member_id), 0::bigint
    from bucketized b
    where (
      select count(pm.member_id) from page_members pm
      where pm.view_id = b.view_id and pm.member_id = b.member_id
    ) = 1
    group by b.view_id
  loop
    -- update number of members bucketized and remember the last ID
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
