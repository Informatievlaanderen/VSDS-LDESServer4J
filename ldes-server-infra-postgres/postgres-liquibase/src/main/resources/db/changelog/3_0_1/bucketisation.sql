insert into fragmentation_bucketisation (view_name, fragment_id, member_id, sequence_nr)
SELECT fetch_allocation.view_name, fetch_allocation.fragment_id, fetch_allocation.member_id, ingest_ldesmember.sequence_nr FROM fetch_allocation
JOIN ingest_ldesmember on fetch_allocation.member_id=ingest_ldesmember.id