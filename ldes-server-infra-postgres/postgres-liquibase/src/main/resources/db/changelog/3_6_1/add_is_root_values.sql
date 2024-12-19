WITH root_page_partial_urls AS (
    SELECT CONCAT('/', c.name, '/', v.name) AS partial_url
    FROM collections c
    JOIN views v ON c.collection_id = v.collection_id
)
UPDATE pages SET is_root = true, immutable = false
WHERE partial_url IN (SELECT partial_url FROM root_page_partial_urls);