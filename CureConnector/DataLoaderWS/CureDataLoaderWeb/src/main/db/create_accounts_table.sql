create table ws_accounts (account_id character varying(80), token character varying(80), description character varying(200), enabled boolean);

alter table ws_accounts add constraint accounts_pk PRIMARY KEY(account_id);