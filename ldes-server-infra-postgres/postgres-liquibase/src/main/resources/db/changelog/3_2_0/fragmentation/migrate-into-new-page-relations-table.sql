INSERT INTO page_relations (from_page_id, to_page_id, value, value_type, relation_type)
WITH fragment_relations_page_relations_cte AS
             (SELECT)
;

INSERT INTO page_relations (from_page_id, to_page_id, relation_type, path, value, value_type)
SELECT  ffr.fragment_id, ffr.tree_node, ffr.relation, ffr.tree_path, ffr.tree_value, ffr.tree_value_type
    FROM fragmentation_fragment_relations ffr;



SELECT *
FROM pages;
SELECT *
FROM fragmentation_fragment_relations;

SELECT *
FROM buckets;
