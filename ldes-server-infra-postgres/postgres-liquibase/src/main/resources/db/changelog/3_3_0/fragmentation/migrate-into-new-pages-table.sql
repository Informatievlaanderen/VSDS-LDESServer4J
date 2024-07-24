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
UNION
SELECT NULL AS bucket_id, delete_time, id
FROM fragmentation_fragment
WHERE id NOT IN (SELECT fragment_id FROM fragmentation_bucketisation);