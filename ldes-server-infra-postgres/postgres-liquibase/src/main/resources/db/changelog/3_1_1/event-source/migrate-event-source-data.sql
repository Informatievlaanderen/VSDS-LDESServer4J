INSERT INTO eventsources (collection_id, retention_policies)
SELECT c.collection_id, e.retention_policies
FROM eventsource e
         JOIN collections c ON e.collection_name = c.name;