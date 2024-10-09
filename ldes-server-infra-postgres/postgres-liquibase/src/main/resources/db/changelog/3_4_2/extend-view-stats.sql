-- add last ID columns
ALTER TABLE "view_stats" ADD COLUMN "bucketized_last_id" bigint NOT NULL default 0;
ALTER TABLE "view_stats" ADD COLUMN "paginated_last_id" bigint NOT NULL default 0;
ALTER TABLE "view_stats" ADD PRIMARY KEY ("view_id");

-- initialize last ID values
UPDATE view_stats vs SET bucketized_last_id = (select max(distinct(member_id)) from page_members pm WHERE pm.view_id = vs.view_id);
UPDATE view_stats vs SET paginated_last_id = (select max(distinct(member_id)) from page_members pm WHERE pm.view_id = vs.view_id and pm.page_id is not null);

-- fix function to also set view stats last bucketized ID
create or replace function on_page_member_inserted() returns trigger language plpgsql as $$
begin
  if ((select count(member_id) from page_members
       where view_id = NEW.view_id and member_id = NEW.member_id) = 1) then
    update view_stats set bucketized_count = bucketized_count + 1 where view_id = NEW.view_id;
  end if;
  update view_stats set bucketized_last_id = NEW.member_id where view_id = NEW.view_id;
  return null;
end
$$;

-- fix function to also set view stats last paginated ID
create or replace function on_page_member_updating() returns trigger language plpgsql as $$
begin
  if (OLD.page_id is null and NEW.page_id is not null) then
    if ((select count(member_id) from page_members
         where view_id = NEW.view_id and member_id = NEW.member_id) = 1) then
      update view_stats set paginated_count = paginated_count + 1 where view_id = NEW.view_id;
    end if;
    update view_stats set paginated_last_id = NEW.member_id where view_id = NEW.view_id;
  end if;
  return NEW;
end
$$;
