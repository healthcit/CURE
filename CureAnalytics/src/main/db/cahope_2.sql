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
CREATE TABLE "CaHope".report_templates
(
  id bigint NOT NULL DEFAULT nextval('report_template_id_seq'::regclass),
  report character varying(10000),
  title text,
  "timestamp" timestamp without time zone DEFAULT now(),
  "shared" boolean,
  "owner_id" bigint,
  CONSTRAINT pk_report_template_id PRIMARY KEY (id),
  CONSTRAINT pk_id_unique UNIQUE (id),
  CONSTRAINT report_owner_fk FOREIGN KEY (owner_id)
      REFERENCES "CaHope"."user" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "CaHope".report_templates OWNER TO cahope;


CREATE OR REPLACE FUNCTION "CaHope".update_lastmodified_column()
  RETURNS trigger AS
$BODY$
  BEGIN
    NEW.timestamp = NOW();
    RETURN NEW;
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "CaHope".update_lastmodified_column() OWNER TO cahope;


CREATE TRIGGER update_lastmodified_modtime
  BEFORE UPDATE
  ON "CaHope".report_templates
  FOR EACH ROW
  EXECUTE PROCEDURE "CaHope".update_lastmodified_column();
  
  CREATE TABLE "CaHope"."user"
(
  id bigint NOT NULL,
  username character varying(25),
  "password" character varying(130),
  created_date date,
  email_addr character varying(25),
  CONSTRAINT users_pri_key PRIMARY KEY (id),
  CONSTRAINT unique_username UNIQUE (username)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "CaHope"."user" OWNER TO cahope;


CREATE TABLE "CaHope"."role"
(
  id bigint NOT NULL,
  "name" character varying(20),
  display_name character varying(20),
  CONSTRAINT roles_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "CaHope"."role" OWNER TO cahope;

CREATE TABLE "CaHope".user_role
(
  user_id bigint NOT NULL,
  role_id bigint NOT NULL,
  CONSTRAINT "fk_roleId_user_role" FOREIGN KEY (role_id)
      REFERENCES "CaHope"."role" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT "fk_userId_user_role" FOREIGN KEY (user_id)
      REFERENCES "CaHope"."user" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "CaHope".user_role OWNER TO cahope;

INSERT INTO role VALUES (1, 'ROLE_USER', 'User');
INSERT INTO role VALUES (2, 'ROLE_ADMIN', 'Administrator');

INSERT INTO "user" VALUES (1, 'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', '2012-06-04', 'admin@admin.com');

INSERT INTO user_role VALUES (1, 1);
INSERT INTO user_role VALUES (1, 2);



