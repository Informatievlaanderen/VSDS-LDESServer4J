UPDATE page_members pm
SET view_id = (select b.view_id from buckets b where b.bucket_id = pm.bucket_id)
;
CREATE FUNCTION add_view_id_if_missing()
RETURNS trigger AS $$
BEGIN
  IF NEW.view_id IS NULL THEN
    NEW.view_id := (select b.view_id from buckets b where b.bucket_id = NEW.bucket_id);
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql
;
CREATE TRIGGER page_members_set_view_id
BEFORE INSERT ON page_members
FOR EACH ROW
EXECUTE PROCEDURE add_view_id_if_missing()
;
