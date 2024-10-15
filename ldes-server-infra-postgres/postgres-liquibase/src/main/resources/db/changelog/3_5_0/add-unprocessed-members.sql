create view unprocessed_members as
SELECT m.member_id, c.collection_id, v.view_id
FROM collections c
JOIN views v ON v.collection_id = c.collection_id
JOIN view_stats vs ON vs.view_id = v.view_id
join processable_members m on m.collection_id = c.collection_id
WHERE m.member_id > vs.bucketized_last_id
;
