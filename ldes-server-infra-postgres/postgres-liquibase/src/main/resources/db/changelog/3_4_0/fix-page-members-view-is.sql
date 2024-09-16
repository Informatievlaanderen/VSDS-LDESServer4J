ALTER TABLE "page_members"
DROP CONSTRAINT "fk_view_id",
ADD FOREIGN KEY ("fk_view_id") REFERENCES "views" ("view_id") ON DELETE CASCADE ON UPDATE NO ACTION
