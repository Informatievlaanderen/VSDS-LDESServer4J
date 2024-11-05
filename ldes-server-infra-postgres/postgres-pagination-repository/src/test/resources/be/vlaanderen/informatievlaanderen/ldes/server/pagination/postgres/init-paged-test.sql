WITH new_collection AS (
    INSERT INTO collections (name, timestamp_path, version_of_path, version_delimiter, is_closed, skolemization_domain)
        VALUES ('es', '', '', NULL, false, null)
        RETURNING collection_id
),
     new_view AS (
         INSERT INTO views (collection_id, name, fragmentations, retention_policies, page_size)
             SELECT collection_id, 'es/view', '[]', '[]', 10 FROM new_collection
             RETURNING view_id
     ),
     new_bucket AS (
         INSERT INTO buckets (bucket, view_id)
             SELECT '', view_id FROM new_view
             RETURNING bucket_id
     )
INSERT INTO pages (bucket_id, partial_url, immutable)
SELECT bucket_id, '/es/view?pageNumber=1', false FROM new_bucket;