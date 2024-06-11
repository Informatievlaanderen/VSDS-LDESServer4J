WITH retention_policies AS (SELECT e.collection_name,
                                   jsonb_array_elements_text(e.retention_policies) AS policy
                            FROM eventsource e
                            WHERE e.retention_policies != '[]'
                            UNION
                            SELECT e.collection_name, '' AS policy
                            FROM eventsource e
                            WHERE e.retention_policies = '[]')
INSERT
INTO eventsources(collection_id, retention_policies)
SELECT c.collection_id, string_agg(rp.policy, E'\n') as retention_policies
FROM eventsource e
         JOIN retention_policies rp ON e.collection_name = rp.collection_name
         JOIN collections c ON c.name = e.collection_name
GROUP BY c.collection_id, e.collection_name