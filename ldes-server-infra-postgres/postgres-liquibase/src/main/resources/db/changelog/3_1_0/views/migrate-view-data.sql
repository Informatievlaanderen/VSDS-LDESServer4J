WITH retention_policies AS (SELECT v.view_name,
                                   jsonb_array_elements_text(v.retention_policies) AS policy
                            FROM view v
                            WHERE v.retention_policies != '[]'
                            UNION
                            SELECT v.view_name, '' AS policy
                            FROM view v
                            WHERE retention_policies = '[]')
INSERT
INTO views (collection_id, name, fragmentations, page_size, retention_policies)
SELECT c.collection_id,
       SPLIT_PART(v.view_name, '/', 2),
       v.fragmentations::jsonb,
       v.page_size,
       string_agg(rp.policy, E'\n') as retention_policies
FROM view v
         JOIN retention_policies rp ON v.view_name = rp.view_name
         JOIN collections c ON c.name = SPLIT_PART(v.view_name, '/', 1)
GROUP BY c.collection_id, v.view_name;