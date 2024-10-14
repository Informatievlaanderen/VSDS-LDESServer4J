-- create collection statistics table
CREATE TABLE collection_stats
(
    collection_id bigint NOT NULL,
    ingested_count bigint NOT NULL default 0,
    ingested_last_id bigint NOT NULL default 0
);
ALTER TABLE "collection_stats" ADD FOREIGN KEY ("collection_id") REFERENCES "collections" ("collection_id") ON DELETE CASCADE;
ALTER TABLE "collection_stats" ADD PRIMARY KEY ("collection_id");

-- initialize current counts
insert into collection_stats(collection_id, ingested_count, ingested_last_id)
select c.collection_id, count(m.member_id), max(m.member_id)
from members m join collections c on m.collection_id = c.collection_id
group by c.collection_id;

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
