-- create collection statistics table
CREATE TABLE collection_stats
(
    collection_id bigint NOT NULL,
    ingested_count bigint NOT NULL default 0,
    ingested_last_id bigint NOT NULL default 0
);
ALTER TABLE "collection_stats" ADD FOREIGN KEY ("collection_id") REFERENCES "collections" ("collection_id") ON DELETE CASCADE;
ALTER TABLE "collection_stats" ADD PRIMARY KEY ("collection_id");

-- create function to insert a stats row for the new collection
create function on_collection_inserted() returns trigger language plpgsql as $$
begin
  insert into collection_stats(collection_id) values (NEW.collection_id);
  return null;
end
$$;

-- add stats row after collection inserted
create trigger collections_ai after insert on collections
for each row execute procedure on_collection_inserted();


-- initialize current counts
insert into collection_stats(collection_id, ingested_count, ingested_last_id)
select c.collection_id, count(m.member_id), max(m.member_id)
from members m join collections c on m.collection_id = c.collection_id
group by c.collection_id;

-- create function to update collection stats when member ingested
create function on_member_inserted() returns trigger language plpgsql as $$
begin
  update collection_stats
  set ingested_count = ingested_count + 1, ingested_last_id = NEW.member_id
  where collection_id = NEW.collection_id;
  return null;
end
$$;

-- update stats row after member inserted
create trigger member_ai after insert on members
for each row execute procedure on_member_inserted();
