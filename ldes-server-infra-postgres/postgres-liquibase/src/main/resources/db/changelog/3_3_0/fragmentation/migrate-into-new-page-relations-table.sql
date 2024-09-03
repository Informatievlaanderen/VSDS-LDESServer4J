INSERT INTO page_relations (from_page_id, to_page_id, relation_type, path, value, value_type)
SELECT frompages.page_id,
       topages.page_id,
       ffr.relation,
       ffr.tree_path,
       ffr.tree_value,
       ffr.tree_value_type
FROM fragmentation_fragment_relations ffr
         JOIN pages frompages ON ffr.fragment_id = frompages.partial_url
         JOIN pages topages ON ffr.tree_node = topages.partial_url;