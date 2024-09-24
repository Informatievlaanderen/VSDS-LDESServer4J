INSERT INTO collections
VALUES (1, 'collection', 'http://purl.org/dc/terms/created', 'http://purl.org/dc/terms/isVersionOf', false,
        false);

INSERT INTO views VALUES (1, 1, 'name', '[]', '', 150);

INSERT INTO buckets (bucket_id, bucket, view_id) VALUES (1, 'key=value&k=v', 1);
INSERT INTO buckets (bucket_id, bucket, view_id) VALUES (2, '', 1)