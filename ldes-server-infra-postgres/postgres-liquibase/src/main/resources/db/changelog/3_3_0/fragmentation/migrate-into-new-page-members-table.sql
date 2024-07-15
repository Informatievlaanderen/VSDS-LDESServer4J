INSERT INTO page_members (bucket_id, member_id, page_id)
SELECT b.bucket_id, m.member_id, p.page_id AS page_id
FROM fragmentation_bucketisation fb
         JOIN members m
              ON fb.member_id = m.old_id
         JOIN buckets b
              ON SPLIT_PART(fb.fragment_id, '?', 2) = b.bucket
         JOIN pages p ON p.bucket_id = b.bucket_id;