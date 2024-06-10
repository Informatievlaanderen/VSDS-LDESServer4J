INSERT INTO views (collection_id, page_size, name, fragmentations, retention_policies)
SELECT c.collection_id, v.page_size, SPLIT_PART(v.view_name, '/', 2), v.fragmentations::jsonb, v.retention_policies
FROM view v
         JOIN collections c ON c.name = SPLIT_PART(v.view_name, '/', 1)