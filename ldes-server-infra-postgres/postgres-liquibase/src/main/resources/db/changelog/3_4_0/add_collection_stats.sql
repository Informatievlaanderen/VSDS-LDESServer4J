create or replace view collection_stats as
SELECT c.collection_id, vs.view_id, c.name as collection_name, v.name as view_name, ms.last as member_count, vs.bucketized_count, vs.paginated_count,
  (ms.last - bucketized_count) as to_bucketize, (ms.last - paginated_count) as to_paginate,
  round((bucketized_count::float / ms.last * 100)::numeric, 2) as bucketized_percentage,
  round((paginated_count::float / ms.last * 100)::numeric, 2) as paginated_percentage
FROM view_stats vs
join member_stats ms on ms.view_id = vs.view_id
join views v on v.view_id = ms.view_id
join collections c on c.collection_id = v.collection_id
ORDER BY collection_id, view_id
