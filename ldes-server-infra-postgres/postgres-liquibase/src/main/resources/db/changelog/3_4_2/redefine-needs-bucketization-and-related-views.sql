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

-- drop member stats
DROP VIEW "member_stats";
DROP VIEW "bucket_stats";
