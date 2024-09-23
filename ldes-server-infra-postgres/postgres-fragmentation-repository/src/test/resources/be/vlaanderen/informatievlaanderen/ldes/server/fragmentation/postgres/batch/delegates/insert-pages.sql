INSERT INTO buckets (bucket_id, bucket, view_id) VALUES (4, 'year=2023&month=07', 1);

INSERT INTO pages (page_id, bucket_id, partial_url)
VALUES (1, 1, '/mobility-hindrances/by-hour'),
       (2, 2, '/mobility-hindrances/by-hour?year=2023'),
       (3, 3, '/mobility-hindrances/by-hour?year=2023&month=06'),
       (4, 4, '/mobility-hindrances/by-hour?year=2023&month=07')