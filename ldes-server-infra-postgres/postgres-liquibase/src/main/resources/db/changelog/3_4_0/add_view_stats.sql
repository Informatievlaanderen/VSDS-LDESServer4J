-- create view statistics table
CREATE TABLE view_stats
(
    view_id bigint NOT NULL,
    bucketized_count bigint NOT NULL default 0,
    paginated_count bigint NOT NULL default 0
);
ALTER TABLE "view_stats" ADD FOREIGN KEY ("view_id") REFERENCES "views" ("view_id");

-- create function to insert a stats row for the new view
create function on_view_inserted() returns trigger language plpgsql as $$
begin
  insert into view_stats(view_id) values (NEW.view_id);
  return null;
end
$$;

-- add stats row after view inserted
create trigger views_ai after insert on views
for each row execute procedure on_view_inserted();

-- create function to delete stats row for a view
create function on_view_deleted() returns trigger language plpgsql as $$
begin
  delete from view_stats where view_id = OLD.view_id;
  return null;
end
$$;

-- remove stats row after view deleted
create trigger views_ad after delete on views
for each row execute procedure on_view_deleted();

-- initialize current counts
update view_stats vs
set
 bucketized_count = (select count(*) from page_members pm WHERE pm.view_id = vs.view_id),
 paginated_count = (select count(*) from page_members pm WHERE pm.view_id = vs.view_id and pm.page_id is not null)
;

-- create function to increase view stats count when member bucketized
create function on_page_member_inserted() returns trigger language plpgsql as $$
begin
  update view_stats set bucketized_count = bucketized_count + 1;
  return null;
end
$$;

-- update stats row after page_member inserted
create trigger page_member_ai after insert on page_members
for each row execute procedure on_page_member_inserted();

-- create function to increase view stats count when member paginated
create function on_page_member_updating() returns trigger language plpgsql as $$
begin
  if (OLD.page_id is null and NEW.page_id is not null) then
    update view_stats set paginated_count = paginated_count + 1;
  end if;
  return NEW;
end
$$;

-- update stats row before page_member updating
create trigger page_member_bu before update on page_members
for each row execute procedure on_page_member_updating();
