---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------

CREATE SCHEMA gateway_etl;


ALTER SCHEMA gateway_etl OWNER TO etl;

SET search_path = gateway_etl, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;


-- Table form

CREATE TABLE "gateway_etl"."form"(
 "uuid" Character varying(40) NOT NULL,
 "parent_id" Character varying(40),
 "name" Character varying(100),
 "multiple_instances" Boolean
)
WITH (OIDS=FALSE)
;

-- Add keys for table form

ALTER TABLE "gateway_etl"."form" ADD CONSTRAINT "form_pk" PRIMARY KEY ("uuid")
;

-- Table: question_element

-- DROP TABLE question_element;

CREATE TABLE question_element
(
  uuid character varying(40),
  form_uuid character varying(40),
  description character varying(500),
  question_uuid character varying(40),
  question_short_name character varying(250),
  question_ord integer,
  question_type character varying(100),
  answer_uuid character varying(40),
  answer_type character varying(25),
  answer_value_constraint character varying(100),
  av_value character varying(250),
  av_description character varying(500),
  av_ord integer,
  av_uuid character varying(40),
  question_is_readonly boolean NOT NULL DEFAULT false,
  input_data_question_ord integer
)
WITH (
  OIDS=FALSE
);
ALTER TABLE question_element OWNER TO etl;


CREATE TABLE table_element
(
  uuid character varying(40),
  form_uuid character varying(40),
  description character varying(500),
  table_short_name character varying(250),
  table_type character varying(15),
  question_uuid character varying(40),
  question_short_name character varying(250),
  question_ord integer,
  question_is_identifying boolean,
  question_type character varying(100),
  answer_uuid character varying(40),
  answer_type character varying(25),
  answer_value_constraint character varying(100),
  av_value character varying(250),
  av_description character varying(500),
  av_ord integer,
  av_uuid character varying(40),
  question_is_readonly boolean NOT NULL DEFAULT false,
  input_data_question_ord integer
)
WITH (
  OIDS=FALSE
);
ALTER TABLE table_element OWNER TO etl;

-- Trigger: update_tbl_input_data_question_ord_trig on table_element

-- DROP TRIGGER update_tbl_input_data_question_ord_trig ON table_element;

CREATE TRIGGER update_tbl_input_data_question_ord_trig
  BEFORE UPDATE
  ON table_element
  FOR EACH ROW
  EXECUTE PROCEDURE update_tbl_input_data_question_ord();

-- Create relationships section ------------------------------------------------- 

ALTER TABLE "gateway_etl"."question_element" ADD CONSTRAINT "form_question_element" FOREIGN KEY ("form_uuid") REFERENCES "gateway_etl"."form" ("uuid") ON DELETE CASCADE ON UPDATE NO ACTION
;

ALTER TABLE "gateway_etl"."table_element" ADD CONSTRAINT "form_table_element" FOREIGN KEY ("form_uuid") REFERENCES "gateway_etl"."form" ("uuid") ON DELETE CASCADE ON UPDATE NO ACTION
;

-- Triggers ---------------------------------------------------------------------

CREATE OR REPLACE FUNCTION update_input_data_question_ord()
  RETURNS trigger AS
$BODY$
BEGIN
	NEW.input_data_question_ord := NEW.question_ord;
RETURN NEW;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_input_data_question_ord() OWNER TO etl;

-- Function: update_tbl_input_data_question_ord()

-- DROP FUNCTION update_tbl_input_data_question_ord();

CREATE OR REPLACE FUNCTION update_tbl_input_data_question_ord()
  RETURNS trigger AS
$BODY$
BEGIN
IF NEW.table_type = 'STATIC' THEN
	NEW.input_data_question_ord := coalesce(NEW.question_ord,1)-1;
ELSE
	NEW.input_data_question_ord := NEW.question_ord;
END IF;
RETURN NEW;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_tbl_input_data_question_ord() OWNER TO etl;

CREATE TRIGGER update_input_data_question_ord_trig
  BEFORE UPDATE
  ON question_element
  FOR EACH ROW
  EXECUTE PROCEDURE update_input_data_question_ord();
  
CREATE TRIGGER update_tbl_input_data_question_ord_trig
  BEFORE UPDATE
  ON table_element
  FOR EACH ROW
  EXECUTE PROCEDURE update_tbl_input_data_question_ord();
  
  -- Function: get_form_level(character varying)

-- DROP FUNCTION get_form_level(character varying);

CREATE OR REPLACE FUNCTION get_form_level(formuuid character varying)
  RETURNS integer AS
$BODY$

DECLARE formlevel integer;
BEGIN

WITH RECURSIVE ancestors(form_id, parent_id, form_level) AS
(SELECT f.uuid, f.parent_id, 1 from form f where coalesce(f.parent_id,'')='' UNION SELECT f.uuid, f.parent_id, a.form_level+1 from form f, ancestors a where f.parent_id = a.form_id)
SELECT form_level from ancestors WHERE form_id=formuuid INTO formlevel;


RETURN formlevel;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION get_form_level(character varying) OWNER TO etl;

-- Function: get_root_form(character varying)

-- DROP FUNCTION get_root_form(character varying);

CREATE OR REPLACE FUNCTION get_root_form(formuuid character varying)
  RETURNS character varying AS
$BODY$

DECLARE rootform character varying;
BEGIN

WITH RECURSIVE ancestors(form_id, parent_id, root_form) AS
(SELECT f.uuid, f.parent_id, f.uuid from form f where coalesce(f.parent_id,'')='' UNION SELECT f.uuid, f.parent_id, a.root_form from form f, ancestors a where f.parent_id = a.form_id)
SELECT root_form from ancestors WHERE form_id=formuuid INTO rootform;


RETURN rootform;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION get_root_form(character varying) OWNER TO etl;







