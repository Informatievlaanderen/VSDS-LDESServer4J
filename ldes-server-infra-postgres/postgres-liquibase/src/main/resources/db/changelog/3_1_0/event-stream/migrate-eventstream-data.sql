INSERT INTO collections (create_versions, is_closed, name, timestamp_path, version_of_path)
SELECT e.version_creation_enabled, e.is_closed, e.id, e.timestamp_path, e.version_of_path
FROM eventstream e;