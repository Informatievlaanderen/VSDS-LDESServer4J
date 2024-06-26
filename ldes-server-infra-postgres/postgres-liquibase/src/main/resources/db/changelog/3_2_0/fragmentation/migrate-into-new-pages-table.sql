-- INSERT INTO pages (bucket_id, expiration, partial_url)
-- WITH bucket_fragment_ids AS
--          (SELECT b.bucket_id, CONCAT('/', c.name, '/', v.name, nullif('?' || b.bucket, '?')) AS fragment_id
--           FROM views v
--                    JOIN collections c
--                         ON v.collection_id = c.collection_id
--                    JOIN buckets b
--                         ON v.view_id = b.view_id)
-- SELECT DISTINCT ff.delete_time, b.fragment_id AS partial_url, bucket_id
-- FROM fetch_allocation fa
--          JOIN bucket_fragment_ids b ON b.fragment_id = REGEXP_REPLACE(fa.fragment_id, '&[^&]*$', '')
--          JOIN fragmentation_fragment ff ON ff.id = fa.fragment_id
-- UNION
-- SELECT ff.delete_time, ff.id AS fragment_id, NULL
--        FROM fragmentation_fragment ff
-- WHERE ff.id NOT IN (SELECT b.fragment_id FROM bucket_fragment_ids b);
--
-- SELECT * FROM fragmentation_fragment;

INSERT INTO pages (bucket_id, expiration, partial_url)
WITH empty_frag_views AS
         (SELECT v.view_id, c.name || '/' || v.name AS view_name
          FROM views v
                   JOIN collections c ON v.collection_id = c.collection_id
          WHERE v.fragmentations = '[]')
SELECT b.bucket_id, ff.delete_time, ff.id
FROM fragmentation_fragment ff
         JOIN empty_frag_views efv ON ff.id = '/' || efv.view_name
         JOIN buckets b ON b.view_id = efv.view_id
UNION
SELECT b.bucket_id, ff.delete_time, ff.id AS partial_url
FROM fragmentation_fragment ff
         JOIN buckets b ON ff.id LIKE '%?' || b.bucket


-- UNION
-- SELECT ff.delete_time, ff.id, b.bucket_id
-- FROM fragmentation_fragment ff
-- JOIN buckets b ON ff.id NOT LIKE '%?%'

