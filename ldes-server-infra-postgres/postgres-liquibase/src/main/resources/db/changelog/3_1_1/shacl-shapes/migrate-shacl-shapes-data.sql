INSERT INTO shacl_shapes (collection_id, model)
SELECT c.collection_id, s.model
FROM shacl_shape s
         JOIN collections c ON s.id = c.name;