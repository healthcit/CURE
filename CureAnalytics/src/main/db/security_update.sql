------------------------------------------------------------------------------
-- Copyright (c) 2013 HealthCare It, Inc.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the BSD 3-Clause license
-- which accompanies this distribution, and is available at
-- http://directory.fsf.org/wiki/License:BSD_3Clause
-- 
-- Contributors:
--     HealthCare It, Inc - initial API and implementation
------------------------------------------------------------------------------
CREATE SEQUENCE "CaHope"."user_seq"
    START WITH 6
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "CaHope"."user_seq" OWNER TO cahope;

CREATE TABLE "CaHope".user
(
  id bigint NOT NULL,
  username character varying(25),
  "password" character varying(130),
  created_date date,
  email_addr character varying(25),
  CONSTRAINT users_pri_key PRIMARY KEY (id),
  CONSTRAINT unique_username UNIQUE (username)
);

CREATE TABLE "CaHope".role
(
  id bigint NOT NULL,
  "name" character varying(20),
  "display_name" character varying(20),
  CONSTRAINT roles_pkey PRIMARY KEY (id)
);

CREATE TABLE "CaHope".user_role
(
  user_id bigint NOT NULL,
  role_id bigint NOT NULL,
  CONSTRAINT "fk_roleId_user_role" FOREIGN KEY (role_id)
  REFERENCES "CaHope".role (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT "fk_userId_user_role" FOREIGN KEY (user_id)
  REFERENCES "CaHope".user (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE CASCADE
);

ALTER TABLE "CaHope".role OWNER TO cahope;
ALTER TABLE "CaHope".user OWNER TO cahope;
ALTER TABLE "CaHope".user_role OWNER TO cahope;

INSERT INTO "CaHope".role(id, name, display_name) VALUES (1, 'ROLE_USER', 'User');
INSERT INTO "CaHope".role(id, name, display_name) VALUES (2, 'ROLE_ADMIN', 'Administrator');

INSERT INTO "CaHope".user(id, username, password, created_date, email_addr) VALUES (1, 'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', current_date, 'admin@admin.com');
INSERT INTO "CaHope".user_role(user_id, role_id) VALUES (1, 1);
INSERT INTO "CaHope".user_role(user_id, role_id) VALUES (1, 2);