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
create function on_members_inserted() returns trigger language plpgsql as $$
declare
  _stats collection_stats%ROWTYPE;
  -- _count bigint;
begin
  -- select count(*) into _count from ingested;
  -- raise WARNING 'on_members_inserted: % records in "ingested" table', _count;
  for _stats in
    select collection_id, count(member_id)::bigint, max(member_id)
    from ingested
    group by collection_id
  loop
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

-- drop needs_bucketization and related
DROP VIEW "unprocessed_views";
DROP VIEW "needs_bucketization";

-- use collection stats for needs bucketization
CREATE OR REPLACE VIEW "needs_bucketization" AS
SELECT cs.collection_id, v.view_id, (pm.member_id is null) AS should_bucketize
from collection_stats cs
join views v on v.collection_id = cs.collection_id
left outer join page_members pm on pm.view_id = v.view_id and pm.member_id = cs.ingested_last_id
group by cs.collection_id, v.view_id, pm.member_id;

-- recreate unprocessed_views
CREATE OR REPLACE VIEW "unprocessed_views" AS
SELECT c.collection_id, c.name AS collection_name, v.view_id, v.name AS view_name
FROM collections c
JOIN views v ON c.collection_id = v.collection_id
JOIN needs_bucketization nb ON nb.collection_id = c.collection_id AND nb.view_id = v.view_id
JOIN needs_pagination np ON np.collection_id = c.collection_id AND np.view_id = v.view_id
WHERE nb.should_bucketize OR np.should_paginate;

-- drop obsolete member and bucket stats
DROP VIEW "member_stats";
DROP VIEW "bucket_stats";
