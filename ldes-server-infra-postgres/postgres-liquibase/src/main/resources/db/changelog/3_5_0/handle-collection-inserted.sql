-- create function to insert a stats row for the new collection
create function on_collection_inserted() returns trigger language plpgsql as $$
begin
  -- auto-create collection_stats record for each new collection
  insert into collection_stats(collection_id) values (NEW.collection_id);
  return null;
end
$$;

-- add stats row after collection inserted
create trigger collections_ai after insert on collections
for each row execute procedure on_collection_inserted();
