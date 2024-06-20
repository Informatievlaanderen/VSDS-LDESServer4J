INSERT INTO member_buckets (bucket_id, member_id)
SELECT b.bucket_id, m.member_id
FROM fragmentation_bucketisation fb
         JOIN members m
              ON fb.member_id = m.subject
         JOIN buckets b
              ON fb.fragment_id = b.bucket;