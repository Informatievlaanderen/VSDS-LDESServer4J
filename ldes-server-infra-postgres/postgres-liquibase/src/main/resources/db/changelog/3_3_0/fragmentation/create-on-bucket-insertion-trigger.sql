CREATE TRIGGER insert_page_on_bucket_insertion
    AFTER INSERT
    ON buckets
    FOR EACH ROW
EXECUTE PROCEDURE on_bucket_insertion();
-- end