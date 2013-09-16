---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------


CREATE SEQUENCE all_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE all_id_seq OWNER TO cacure;

/* form_skip_answers table */

CREATE TABLE form_skip_answers
(
  id serial NOT NULL,
  owner_id character varying(100),
  trigger_form_id character varying(100),
  trigger_instance_id bigint,
  trigger_question_id character varying(100),
  answer_value character varying(150),
  form_skip_id bigint NOT NULL,
  CONSTRAINT form_skip_ans_pkey PRIMARY KEY (id),
  CONSTRAINT form_skip_ans_skip_fkey FOREIGN KEY (form_skip_id)
      REFERENCES form_skip (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE form_skip_answers OWNER TO cacure;



/* form_skip_vw view*/

CREATE OR REPLACE VIEW form_skip_vw AS 
 WITH RECURSIVE skip_descendants(trigger_form_id, trigger_instance_id, trigger_question_id, form_id, owner_id, instance_id, logical_op, do_hide) AS (    
   SELECT skip.question_owner_form_id AS trigger_form_id, answers.trigger_instance_id, skip.question_id AS trigger_question_id, 
	sg.form_id, answers.owner_id, sg.instance_id, skip.logical_op, 
	parts.answer_value::text <> COALESCE(answers.answer_value, '||||NOVALUESAVED||||'::character varying)::text AS do_hide
	FROM form_skip skip, skip_parts parts, form_skip_answers answers, sharing_group_form_instance sg
	WHERE skip.id = parts.parent_id AND skip.id = answers.form_skip_id 
	AND sg.form_id = skip.form_id
	AND sg.sharing_group_id::text = answers.owner_id 
	AND (sg.parent_instance_id IS NULL OR COALESCE(sg.parent_instance_id,-1)=answers.trigger_instance_id) AND sg.status <> 'NEW'
   UNION
   SELECT c.trigger_form_id, c.trigger_instance_id, c.trigger_question_id,
	sg.form_id, c.owner_id, sg.instance_id, c.logical_op, c.do_hide
	FROM sharing_group_form_instance sg, skip_descendants c, forms f
	WHERE sg.parent_instance_id = c.instance_id and f.parent_id = c.form_id and f.id = sg.form_id and sg.sharing_group_id = c.owner_id and sg.status <> 'NEW'  
 )
 SELECT * FROM skip_descendants
 ORDER BY trigger_form_id, trigger_instance_id, form_id, instance_id;

ALTER TABLE form_skip_vw OWNER TO cacure;

/* available_parent_form table */
CREATE TABLE available_parent_form
(
  parent_form_id character varying(100) NOT NULL,
  parent_instance_id bigint NOT NULL,
  child_form_id character varying(100) NOT NULL,
  module_id character varying(100) NOT NULL,
  sharing_group_id character varying(100) NOT NULL,
  lastupdated timestamp with time zone DEFAULT now(),
  id serial NOT NULL,
  CONSTRAINT sgf_pk PRIMARY KEY (id),
  CONSTRAINT sgf_forms_fk FOREIGN KEY (child_form_id)
      REFERENCES forms (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT sgf_sharinggrp_fk FOREIGN KEY (sharing_group_id)
      REFERENCES sharing_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE available_parent_form OWNER TO cacure;

/* New Module status */
ALTER TABLE modules ALTER status TYPE character varying(50);
update modules set status='DEPLOYMENT_LOCKED';








