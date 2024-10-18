ALTER TABLE page_relations
ADD CONSTRAINT page_relations_from_page_id_to_page_id_relation_type_value 
UNIQUE (from_page_id, to_page_id, relation_type, value)
;
