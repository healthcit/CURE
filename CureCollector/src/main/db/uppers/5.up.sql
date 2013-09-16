---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------

CREATE OR REPLACE VIEW form_skip_vw AS 
 WITH RECURSIVE skip_descendants(trigger_form_id, trigger_instance_id, trigger_question_id, form_id, owner_id, instance_id, logical_op, do_hide) AS (
                 SELECT skip.question_owner_form_id AS trigger_form_id, answers.trigger_instance_id, skip.question_id AS trigger_question_id, skip.form_id, answers.owner_id, sg.instance_id, skip.logical_op, parts.answer_value::text <> COALESCE(answers.answer_value, '||||NOVALUESAVED||||'::character varying)::text AS do_hide
                   FROM skip_parts parts
              JOIN form_skip skip ON skip.id = parts.parent_id
         JOIN form_skip_answers answers ON skip.id = answers.form_skip_id
    LEFT JOIN sharing_group_form_instance sg ON sg.form_id::text = skip.form_id::text AND sg.sharing_group_id::text = answers.owner_id::text
    AND (sg.parent_instance_id IS NULL OR (sg.parent_instance_id IS NOT NULL AND sg.parent_instance_id=answers.trigger_instance_id))
        UNION 
                 SELECT c.trigger_form_id, c.trigger_instance_id, c.trigger_question_id, sg.form_id, c.owner_id, sg.instance_id, c.logical_op, c.do_hide
                   FROM sharing_group_form_instance sg, skip_descendants c, forms f
                  WHERE sg.parent_instance_id = c.instance_id AND f.parent_id::text = c.form_id::text AND f.id::text = sg.form_id::text AND sg.sharing_group_id::text = c.owner_id::text AND sg.status::text <> 'NEW'::text
        )
 SELECT skip_descendants.trigger_form_id, skip_descendants.trigger_instance_id, skip_descendants.trigger_question_id, skip_descendants.form_id, skip_descendants.owner_id, skip_descendants.instance_id, skip_descendants.logical_op, skip_descendants.do_hide
   FROM skip_descendants
  ORDER BY skip_descendants.trigger_form_id, skip_descendants.trigger_instance_id, skip_descendants.form_id, skip_descendants.instance_id;

ALTER TABLE form_skip_vw OWNER TO cacure;

update modules set status='DEPLOYMENT_LOCKED';
 

