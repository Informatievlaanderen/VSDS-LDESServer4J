CREATE OR REPLACE VIEW member_stats as
select m.collection_id, v.view_id, coalesce(max(m.member_id),0) as last
from members m
         inner join views v on v.collection_id = m.collection_id
group by m.collection_id, v.view_id;

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

CREATE OR REPLACE VIEW needs_pagination as
select c.collection_id, v.view_id, (exists (
    select * from page_members pm
                      inner join buckets b on b.bucket_id = pm.bucket_id
    where pm.page_id is null)) as should_paginate
from collections c
         inner join views v on c.collection_id = v.collection_id
group by c.collection_id, v.view_id;

CREATE OR REPLACE VIEW unprocessed_views as
select c.name as collection_name, v.name as view_name
from collections c
         inner join views v on c.collection_id = v.collection_id
         inner join needs_bucketization nb on nb.collection_id = c.collection_id and nb.view_id = v.view_id
         inner join needs_pagination np on np.collection_id = c.collection_id and np.view_id = v.view_id
where nb.should_bucketize or np.should_paginate;