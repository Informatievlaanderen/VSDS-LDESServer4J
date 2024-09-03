CREATE OR REPLACE VIEW "member_stats" AS
SELECT c.collection_id,
       v.view_id,
       COALESCE(( SELECT max(m.member_id) AS max
                  FROM members m
                  WHERE (m.collection_id = c.collection_id)), (0)::bigint) AS last
FROM (collections c
    JOIN views v ON ((v.collection_id = c.collection_id)))
GROUP BY c.collection_id, v.view_id;

CREATE OR REPLACE VIEW bucket_stats as
select c.collection_id, v.view_id, coalesce(
        (select max(pm.member_id)
         from page_members pm
                  inner join buckets b on pm.bucket_id = b.bucket_id
         where v.view_id = b.view_id)
    ,0) as last
from collections c
         inner join views v on c.collection_id = v.collection_id
group by c.collection_id, v.view_id;

CREATE OR REPLACE VIEW needs_bucketization as
select ms.collection_id, ms.view_id, (ms.last > bs.last) as should_bucketize
from member_stats ms
         inner join bucket_stats bs on bs.collection_id = ms.collection_id and bs.view_id = ms.view_id;

CREATE OR REPLACE VIEW "needs_pagination" AS
SELECT c.collection_id,
       v.view_id,
       (EXISTS ( SELECT pm.bucket_id,
                        pm.member_id,
                        pm.page_id,
                        b.bucket_id,
                        b.bucket,
                        b.view_id
                 FROM (page_members pm
                     JOIN buckets b ON ((b.bucket_id = pm.bucket_id)))
                 WHERE ((pm.page_id IS NULL) AND (b.view_id = v.view_id)))) AS should_paginate
FROM (collections c
    JOIN views v ON ((c.collection_id = v.collection_id)))
GROUP BY c.collection_id, v.view_id;

CREATE OR REPLACE VIEW unprocessed_views as
select c.name as collection_name, v.name as view_name
from collections c
         inner join views v on c.collection_id = v.collection_id
         inner join needs_bucketization nb on nb.collection_id = c.collection_id and nb.view_id = v.view_id
         inner join needs_pagination np on np.collection_id = c.collection_id and np.view_id = v.view_id
where nb.should_bucketize or np.should_paginate;