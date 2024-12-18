DROP VIEW IF EXISTS open_pages;

CREATE VIEW open_pages AS
SELECT DISTINCT ON (p.bucket_id) p.page_id,
                                 p.bucket_id,
                                 p.partial_url,
                                 v.page_size,
                                 count(pm.member_id) AS assigned_members
FROM pages p
    JOIN buckets b ON b.bucket_id = p.bucket_id
    JOIN views v ON v.view_id = b.view_id
    LEFT JOIN page_members pm ON pm.page_id = p.page_id
WHERE NOT p.immutable
GROUP BY p.page_id, v.page_size, p.bucket_id, p.is_root
ORDER BY p.bucket_id, p.is_root
;
