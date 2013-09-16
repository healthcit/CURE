alter table modules add column is_flat boolean;
update modules set is_flat= false;