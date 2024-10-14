-- create function to update collection stats when member ingested
create function on_members_inserted() returns trigger language plpgsql as $$
declare
  _stats collection_stats%ROWTYPE;
begin
  -- for each member inserted
  for _stats in
    select collection_id, count(member_id)::bigint, max(member_id)
    from ingested
    group by collection_id
  loop
    -- update inserted member count and remember last ID
    update collection_stats cs set
      ingested_count = cs.ingested_count + _stats.ingested_count,
      ingested_last_id = greatest(cs.ingested_last_id,_stats.ingested_last_id)
    where collection_id = _stats.collection_id;
  end loop;
  return null;
end
$$;

-- update stats row after members inserted
create trigger members_ai after insert on members
referencing new table as ingested
for statement execute procedure on_members_inserted();
