CREATE OR REPLACE VIEW bucket_stats as
select c.collection_id, v.view_id,
  coalesce((select max(pm.member_id) from page_members pm where v.view_id = pm.view_id),0) as last
from collections c
inner join views v on c.collection_id = v.collection_id
group by c.collection_id, v.view_id;
