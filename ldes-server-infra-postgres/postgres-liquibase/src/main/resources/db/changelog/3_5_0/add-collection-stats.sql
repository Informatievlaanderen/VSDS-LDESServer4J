-- create collection statistics table
CREATE TABLE collection_stats
(
    collection_id bigint NOT NULL,
    ingested_count bigint NOT NULL default 0
);
ALTER TABLE "collection_stats" ADD FOREIGN KEY ("collection_id") REFERENCES "collections" ("collection_id") ON DELETE CASCADE;
ALTER TABLE "collection_stats" ADD PRIMARY KEY ("collection_id");

-- initialize current counts
insert into collection_stats(collection_id, ingested_count)
select c.collection_id, count(m.member_id)
from members m join collections c on m.collection_id = c.collection_id
group by c.collection_id;
