INSERT INTO collection_stats (collection_id)
SELECT collection_id
FROM collections c
WHERE c.collection_id NOT IN (SELECT collection_id FROM collection_stats);