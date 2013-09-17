delete from fileprocess.pre_transaction_details
where pre_transaction_id in (select p.idx from fileprocess.pre_transactions p, fileprocess.submissions s where p.submmission_id = s.idx and s.status='PROCESSED_WITH_NOTIFICATION');

delete from fileprocess.merge_simple
where cast(job_id as integer) in
(select t.idx from fileprocess.transactions t, fileprocess.pre_transactions p, fileprocess.submissions s
where p.submmission_id = s.idx and p.idx = t.ptxn_id and s.status='PROCESSED_WITH_NOTIFICATION');

delete from fileprocess.merge_complex
where cast(job_id as integer) in
(select t.idx from fileprocess.transactions t, fileprocess.pre_transactions p, fileprocess.submissions s
where p.submmission_id = s.idx and p.idx = t.ptxn_id and s.status='PROCESSED_WITH_NOTIFICATION');
