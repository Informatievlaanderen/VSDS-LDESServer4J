INSERT INTO collections (collection_id, name, timestamp_path, version_of_path, version_delimiter, is_closed)
VALUES (1, 'mobility-hindrances', 'http://www.w3.org/ns/prov#generatedAtTime', 'http://purl.org/dc/terms/isVersionOf',
        NULL, false);

INSERT INTO views (view_id, collection_id, name, fragmentations, retention_policies, page_size)
VALUES (1, 1, 'by-hour', '[
  {
    "name": "HierarchicalTimeBasedFragmentation",
    "config": {
      "maxGranularity": "hour",
      "fragmentationPath": "http://www.w3.org/ns/prov#generatedAtTime"
    }
  }
]', '', 250);
