UPDATE page_members pm
SET view_id = (select b.view_id from buckets b where b.bucket_id = pm.bucket_id)
;
