-- remove pagination counting before page member updated
drop trigger page_member_bu on page_members;
drop function on_page_member_updating;

-- create function for pagination counting after page member updated
create or replace function on_page_member_updated() returns trigger language plpgsql as $$
declare
  _count bigint;
begin
  select paginated_count into _count from view_stats where view_id = NEW.view_id;
  if ((select count(member_id) from page_members where view_id = NEW.view_id and member_id = NEW.member_id) = 1) then
    _count = _count + 1;
  end if;
  update view_stats set paginated_count = _count, paginated_last_id = NEW.member_id where view_id = NEW.view_id;
  return NEW;
end
$$;

-- update stats row after page_member updated
create trigger page_member_au after update on page_members
for each row execute procedure on_page_member_updated();
