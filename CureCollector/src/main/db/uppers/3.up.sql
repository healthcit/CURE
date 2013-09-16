---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------

-- Function: validate_instance_id_params(text, text, numeric, numeric, text, numeric)

-- DROP FUNCTION validate_instance_id_params(text, text, numeric, numeric, text, numeric);

CREATE OR REPLACE FUNCTION validate_instance_id_params(formid text, ownerid text, instanceid numeric, parentinstanceid numeric, parentformid text, numnewinstances numeric)
  RETURNS text AS
$BODY$
DECLARE
  message text;
  is_child_form boolean;
  max_instance_id_for_root_instance numeric;
  max_instance_id_for_non_root_instance numeric;
  max_allowed_instances_for_root_instance numeric;
  max_allowed_instances_for_non_root_instance numeric;
  max_allowed_instances numeric;
  is_parent_form_id_correct boolean;
  
BEGIN
  /**************************/
  /* PART A: For all forms: */
  /**************************/

  message := '';
  SELECT EXISTS (select * from forms where id = formid and parent_id is not null) INTO is_child_form;
  SELECT max_instances from forms where id=formid INTO max_allowed_instances;
  
  -- (1) MIN instance_id allowed: instanceIndex must be >= 1 
  IF instanceid < 1 THEN
     message := message || 'Minimum instance ID allowed is 1' || E'\r\n';
  END IF;

  IF is_child_form THEN
	/* For non-root forms: */


	-- (1) Child must have a parent: parentinstanceid must not be null
	IF parentinstanceid is null THEN
          message := message || 'Parent is required for instances with form ID ' || formid || ', owner ID ' || ownerid || E'\r\n';
        END IF;

        -- (2) Child's parentformid must be the correct form ID of its parent
        IF parentformid is null THEN
	   message := message || 'Parent form ID is required for instances with form ID ' || formid || ', owner ID ' || ownerid || E'\r\n';
	ELSE
	   SELECT EXISTS (select parent_id from forms where id = formid and parent_id = parentformid ) INTO is_parent_form_id_correct;
	   IF NOT is_parent_form_id_correct THEN
	      message := message || 'Parent form ID is invalid' || E'\r\n';
	   END IF;
        END IF;

	-- (2) MAX instance_id for non-root instance: 
	-- Multiply the results of this query by [forms].max_instance, the answer must be >= instanceIndex
	select count(sg_parent.*) from sharing_group_form_instance sg_child, sharing_group_form_instance sg_parent, forms f 
	where sg_child.form_id = f.id and sg_parent.form_id = f.parent_id and sg_child.sharing_group_id=sg_parent.sharing_group_id
	and sg_parent.status <> 'NEW'
	and sg_child.form_id=formid and sg_child.sharing_group_id=ownerid
	INTO max_instance_id_for_non_root_instance;
	IF max_allowed_instances * max_instance_id_for_non_root_instance < instanceid THEN
	   message := message || 'Maximum instance ID allowed for form ID ' || formid || ', owner ID ' || ownerid || ' is ' || max_allowed_instances || E'\r\n';
	END IF;

	-- (3) Number of children allowed for non-root instance
	-- This query returns the number of children already saved to the database.
	-- Add the results of this query to the number of NEW instances of this form in the input file with the same parent; 
	-- the answer must not exceed [forms].max_instances 
	
	select count(sg_child.*) from sharing_group_form_instance sg_child, sharing_group_form_instance sg_parent, forms f 
	where sg_child.form_id = f.id and sg_parent.form_id = f.parent_id and sg_child.sharing_group_id=sg_parent.sharing_group_id
	and sg_parent.instance_id = sg_child.parent_instance_id
	and sg_child.status <> 'NEW'
	and sg_child.form_id=formid and sg_child.sharing_group_id=ownerid
	INTO max_allowed_instances_for_non_root_instance;
	IF max_allowed_instances_for_non_root_instance + numnewinstances > max_allowed_instances THEN
           message := message || 'Maximum number of instances allowed for form ID' || formid || ', owner ID ' || ownerid || ' is ' || max_allowed_instances || E'\r\n';
        END IF;
  ELSE

	/* For root forms: */

	-- Set max instance ID for this form: max_instance_id_for_root_instance
	select count(sg_root_instance.*) from sharing_group_form_instance sg_root_instance 
	where sg_root_instance.status <> 'NEW' and sg_root_instance.parent_instance_id is null
	and sg_root_instance.form_id=formid and sg_root_instance.sharing_group_id=ownerid INTO max_instance_id_for_root_instance;

	
	-- (1) MAX instance_id allowed for root instance: The results of this query must be >= instanceIndex 

	IF max_instance_id_for_root_instance < instanceid THEN
	   message := message || 'Maximum instance ID allowed for form ID ' || formid || ', owner ID ' || ownerid || ' is ' || max_instance_id_for_root_instance || E'\r\n';
        END IF;

       -- (2) Number of children allowed for root instance : same query as (1)
       --  Add the results of this query to the number of NEW instances of this form in the input file, 
       --   your answer must be <= [forms].max_instances.
       max_allowed_instances_for_root_instance := max_instance_id_for_root_instance;
       IF max_allowed_instances_for_root_instance + numnewinstances > max_allowed_instances THEN
           message := message || 'Maximum number of instances allowed for form ID ' || formid || ', owner ID ' || ownerid || ' is ' || max_allowed_instances || E'\r\n';
       END IF;
        
       -- (3) parent_instance_id for root instance: parentInstanceIndex must be null 
       IF parentinstanceid is not null THEN
          message := message || 'Parents are not allowed for instances with form ID ' || formid || ', owner ID ' || ownerid || ' is ' || max_allowed_instances || E'\r\n';
       END IF;
  END IF;

return message;

EXCEPTION WHEN OTHERS THEN
     message := 'System error occurred during validation, please contact System Administrator: ' || SQLERRM;
     return message;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION validate_instance_id_params(text, text, numeric, numeric, text, numeric) OWNER TO cacure;




-- Function: get_max_instance_id(text, text, numeric, numeric)

-- DROP FUNCTION get_max_instance_id(text, text, numeric, numeric);

CREATE OR REPLACE FUNCTION get_max_instance_id(formid text, ownerid text, instanceid numeric, parentinstanceid numeric)
  RETURNS numeric AS
$BODY$
DECLARE
  max_instance_id numeric;
  is_child_form boolean;
BEGIN
  select exists (select * from forms where id = formid and parent_id is not null) INTO is_child_form; 

  IF is_child_form THEN
	return (select max(instance_id) from sharing_group_form_instance where form_id = formid and sharing_group_id=ownerid and parent_instance_id=case when parentinstanceid is null then -1 else parentinstanceid end);
  ELSE
        return (select max(instance_id) from sharing_group_form_instance where form_id = formid and sharing_group_id=ownerid);
  END IF;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION get_max_instance_id(text, text, numeric, numeric) OWNER TO cacure;


ALTER TABLE sharing_group_module ADD COLUMN registry_etl_last_transfer_completed_date timestamp without time zone;
ALTER TABLE sharing_group_module ADD COLUMN registry_etl_last_transfer_status character varying(100);
