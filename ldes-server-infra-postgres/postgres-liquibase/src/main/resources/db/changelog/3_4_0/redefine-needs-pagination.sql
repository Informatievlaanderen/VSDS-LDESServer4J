CREATE OR REPLACE VIEW "needs_pagination" AS
SELECT c.collection_id, v.view_id,
  (EXISTS (SELECT * FROM page_members pm WHERE pm.page_id IS NULL AND pm.view_id = v.view_id)) AS should_paginate
FROM collections c
JOIN views v ON c.collection_id = v.collection_id
GROUP BY c.collection_id, v.view_id;
