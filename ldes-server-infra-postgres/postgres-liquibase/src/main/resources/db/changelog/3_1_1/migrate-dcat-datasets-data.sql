INSERT INTO dcat_datasets (collection_id, model)
SELECT c.collection_id, d.model
FROM dcat_dataset d
         JOIN collections c ON d.collection_name = c.name;