-- update current counts
update view_stats vs
set
 bucketized_count = (select count(distinct(member_id)) from page_members pm WHERE pm.view_id = vs.view_id),
 paginated_count = (select count(distinct(member_id)) from page_members pm WHERE pm.view_id = vs.view_id and pm.page_id is not null)
;

-- fix function to increase view stats count when member bucketized
create or replace function on_page_member_inserted() returns trigger language plpgsql as $$
begin
  if ((select count(member_id) from page_members
       where view_id = NEW.view_id and member_id = NEW.member_id) = 1) then
    update view_stats set bucketized_count = bucketized_count + 1 where view_id = NEW.view_id;
  end if;
  return null;
end
$$;

-- fix function to increase view stats count when member paginated
create or replace function on_page_member_updating() returns trigger language plpgsql as $$
begin
  if (OLD.page_id is null and
      NEW.page_id is not null and
      ((select count(member_id) from page_members
        where view_id = NEW.view_id and member_id = NEW.member_id) = 1)) then
    update view_stats set paginated_count = paginated_count + 1 where view_id = NEW.view_id;
  end if;
  return NEW;
end
$$;
