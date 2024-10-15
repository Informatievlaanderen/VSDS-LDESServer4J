create view open_pages as
select p.page_id, p.bucket_id, p.partial_url, v.page_size, COUNT(member_id) AS assigned_members
from pages p
join buckets b on b.bucket_id = p.bucket_id
join views v ON v.view_id = b.view_id
left join page_members pm on pm.page_id = p.page_id
where not p.immutable
group by p.page_id, v.page_size
;
