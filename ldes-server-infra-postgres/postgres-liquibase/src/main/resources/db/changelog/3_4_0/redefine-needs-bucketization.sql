CREATE OR REPLACE VIEW "needs_bucketization" AS
SELECT ms.collection_id, ms.view_id, (pm.member_id is null) AS should_bucketize
FROM member_stats ms
left outer join page_members pm on pm.view_id = ms.view_id and pm.member_id = ms.last
group by ms.collection_id, ms.view_id, pm.member_id
