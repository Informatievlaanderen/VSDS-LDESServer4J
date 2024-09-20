create or replace view bucket_last_page as
select p.bucket_id, max(p.page_id) as last_page_id
from pages p
group by p.bucket_id
;