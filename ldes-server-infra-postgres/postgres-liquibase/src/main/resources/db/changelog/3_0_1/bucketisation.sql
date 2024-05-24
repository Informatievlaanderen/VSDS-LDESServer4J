create table public.bucketisation
(
    id          varchar(255) not null primary key,
    viewName     text,
    fragmentId   text,
    memberId text,
    sequenceNr int
);

insert into bucketisation (id, viewName, fragmentId, memberId, sequenceNr)
SELECT concat_ws('/', fetch_allocation.fragmentId, fetch_allocation.memberId), fetch_allocation.memberId, fetch_allocation.viewName, fetch_allocation.fragmentId, fetch_allocation.memberId, ingest_ldesmember.sequenceNr FROM fetch_allocation
JOIN ingest_ldesmember on fetch_allocation.memberId=ingest_ldesmember.id