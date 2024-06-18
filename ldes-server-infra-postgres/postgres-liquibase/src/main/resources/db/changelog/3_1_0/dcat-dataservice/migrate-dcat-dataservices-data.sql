INSERT INTO dcat_dataservices
SELECT v.view_id, ds.model
FROM dcat_dataservice ds
         JOIN views v ON v.name = SPLIT_PART(ds.view_name, '/', 2);

SELECT * FROM dcat_dataservices;