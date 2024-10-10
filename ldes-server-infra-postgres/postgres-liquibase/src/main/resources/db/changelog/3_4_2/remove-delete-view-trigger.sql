-- remove unneeded trigger, cascading does the job
drop trigger views_ad on views;
drop function on_view_deleted;
