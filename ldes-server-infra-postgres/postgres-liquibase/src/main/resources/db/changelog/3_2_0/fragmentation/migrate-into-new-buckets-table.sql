INSERT INTO buckets (bucket, view_id)
WITH view_names (composed_view_name, view_id) AS
         (SELECT CONCAT(c.name, '/', v.name) AS view_name, v.view_id
          FROM views v
                   JOIN collections c
                        ON v.collection_id = c.collection_id)
SELECT DISTINCT SPLIT_PART(fb.fragment_id, '?', 2) AS bucket, v.view_id
FROM fragmentation_bucketisation fb
         JOIN view_names v
                   ON fb.view_name = v.composed_view_name;