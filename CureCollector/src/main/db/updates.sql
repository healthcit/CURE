---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------

-- New sequence added for form_skip_id

CREATE SEQUENCE cacure.form_skip_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 4
  CACHE 1;
ALTER TABLE cacure.form_skip_id_seq OWNER TO cacuredev;

-- Table: cacure.form_skip

-- DROP TABLE cacure.form_skip;

CREATE TABLE cacure.form_skip
(
  id bigint NOT NULL,
  form_id character varying(100) NOT NULL,
  question_id character varying(100) NOT NULL,
  ans_value character varying(250) NOT NULL,
  "rule" character varying(50) NOT NULL,
  CONSTRAINT pk_id PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cacure.form_skip OWNER TO cacuredev;

-- Column: lastupdated

-- ALTER TABLE cacure.entity_form DROP COLUMN lastupdated;

ALTER TABLE cacure.entity_form ADD COLUMN lastupdated timestamp with time zone;
ALTER TABLE cacure.entity_form ALTER COLUMN lastupdated SET STORAGE PLAIN;


