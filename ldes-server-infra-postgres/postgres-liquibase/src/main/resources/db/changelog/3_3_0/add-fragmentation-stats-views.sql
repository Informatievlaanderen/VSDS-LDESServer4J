CREATE OR REPLACE VIEW member_stats as
select m.collection_id, count(*) as member_count
from members m
group by m.collection_id;

CREATE OR REPLACE VIEW bucket_stats as
select c.collection_id, v.view_id, count(*) as bucketized
from collections c
         inner join views v on c.collection_id = v.collection_id
         inner join buckets b on v.view_id = b.view_id
         inner join page_members pm on b.bucket_id = pm.bucket_id
group by c.collection_id, v.view_id;

CREATE OR REPLACE VIEW page_stats as
select c.collection_id,
       v.view_id,
       (select count(*)
        from page_members pm
                 inner join buckets b on pm.bucket_id = b.bucket_id
        where v.view_id = b.view_id
          and pm.page_id is null) as unpaged
from collections c
         inner join views v on c.collection_id = v.collection_id
group by c.collection_id, v.view_id