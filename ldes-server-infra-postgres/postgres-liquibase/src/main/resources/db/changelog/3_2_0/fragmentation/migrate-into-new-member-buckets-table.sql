INSERT INTO member_buckets (bucket_id, member_id)
SELECT b.bucket_id, m.member_id
FROM fragmentation_bucketisation fb
         JOIN members m
              ON fb.member_id = m.old_id
         JOIN buckets b
              ON SPLIT_PART(fb.fragment_id, '?', 2) = b.bucket;