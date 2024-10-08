CREATE OR REPLACE FUNCTION on_bucket_insertion() RETURNS TRIGGER AS $$
BEGIN
    WITH view_names (view_id, view_name) AS
             (SELECT v.view_id, c.name || '/' || v.name
              FROM views v
                       JOIN collections c ON v.collection_id = c.collection_id)
    INSERT
    INTO pages (bucket_id, expiration, partial_url)
    SELECT NEW.bucket_id, NULL, CONCAT('/', v.view_name, NULLIF('?' || NEW.bucket, '?'))
    FROM view_names v
    WHERE v.view_id = NEW.view_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- end
