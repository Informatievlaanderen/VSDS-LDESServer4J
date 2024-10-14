-- remove pagination counting triggers and functions
drop trigger page_member_ai on page_members;
drop function on_page_member_inserted;

-- remove pagination counting triggers and functions
drop trigger page_member_bu on page_members;
drop function on_page_member_updating;
