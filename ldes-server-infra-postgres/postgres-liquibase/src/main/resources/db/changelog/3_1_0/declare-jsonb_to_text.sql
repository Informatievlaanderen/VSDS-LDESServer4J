CREATE OR REPLACE FUNCTION jsonb_to_text(jsonb_array jsonb)
    RETURNS text AS
$$
BEGIN
    SELECT string_agg(elem, E'\n')
    FROM (SELECT jsonb_array_elements_text(jsonb_array) AS elem) subquery;
END ;
$$
LANGUAGE plpgsql