SELECT c.collection_id,
       c.name AS collection_name,
       v.view_id,
       v.name AS view_name
FROM collections c
         JOIN views v ON c.collection_id = v.collection_id
         JOIN needs_bucketization nb ON nb.collection_id = c.collection_id AND nb.view_id = v.view_id
         JOIN needs_pagination np ON np.collection_id = c.collection_id AND np.view_id = v.view_id
WHERE nb.should_bucketize
   OR np.should_paginate;
