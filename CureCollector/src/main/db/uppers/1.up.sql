---------------------------------------------------------------------------------------------------
-- Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved-
-- Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
-- Proprietary and confidential
---------------------------------------------------------------------------------------------------

ALTER TABLE forms ADD COLUMN instance_group character varying(100);
ALTER TABLE forms ADD COLUMN max_instances bigint NOT NULL DEFAULT 1;
ALTER TABLE forms ADD COLUMN parent_id character varying(100);

ALTER TABLE sharing_group_form ADD COLUMN instance_id bigint NOT NULL DEFAULT 1;
ALTER TABLE sharing_group_form RENAME TO sharing_group_form_instance;

ALTER TABLE sharing_group_form_instance DROP CONSTRAINT entity_form_pk;
ALTER TABLE sharing_group_form_instance ADD CONSTRAINT entity_form_pk PRIMARY KEY (form_id, instance_id, sharing_group_id);

ALTER TABLE sharing_group_form_instance ADD COLUMN creationdate timestamp with time zone DEFAULT now();
UPDATE sharing_group_form_instance set creationdate = lastupdated;




